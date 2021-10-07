/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.URLs;
import org.b3log.symphony.model.*;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.UserForgetPwdValidationMidware;
import org.b3log.symphony.processor.middleware.validate.UserRegister2ValidationMidware;
import org.b3log.symphony.processor.middleware.validate.UserRegisterValidationMidware;
import org.b3log.symphony.service.*;
import org.b3log.symphony.util.HttpUtils;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;

/**
 * Login/Register processor.
 * <ul>
 * <li>Registration (/register), GET/POST</li>
 * <li>Login (/login), GET/POST</li>
 * <li>Logout (/logout), GET</li>
 * <li>Reset password (/reset-pwd), GET/POST</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 2.0.0.1, May 31, 2020
 * @since 0.2.0
 */
@Singleton
public class LoginProcessor {

    /**
     * Wrong password tries.
     * <p>
     * &lt;userId, {"wrongCount": int, "captcha": ""}&gt;
     * </p>
     */
    public static final Map<String, JSONObject> WRONG_PWD_TRIES = new ConcurrentHashMap<>();

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(LoginProcessor.class);

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Pointtransfer management service.
     */
    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Verifycode management service.
     */
    @Inject
    private VerifycodeMgmtService verifycodeMgmtService;

    /**
     * Verifycode query service.
     */
    @Inject
    private VerifycodeQueryService verifycodeQueryService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Invitecode query service.
     */
    @Inject
    private InvitecodeQueryService invitecodeQueryService;

    /**
     * Invitecode management service.
     */
    @Inject
    private InvitecodeMgmtService invitecodeMgmtService;

    /**
     * Invitecode management service.
     */
    @Inject
    private NotificationMgmtService notificationMgmtService;

    /**
     * Role query service.
     */
    @Inject
    private RoleQueryService roleQueryService;

    /**
     * Tag query service.
     */
    @Inject
    private TagQueryService tagQueryService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);
        final UserForgetPwdValidationMidware userForgetPwdValidationMidware = beanManager.getReference(UserForgetPwdValidationMidware.class);
        final UserRegisterValidationMidware userRegisterValidationMidware = beanManager.getReference(UserRegisterValidationMidware.class);
        final UserRegister2ValidationMidware userRegister2ValidationMidware = beanManager.getReference(UserRegister2ValidationMidware.class);

        final LoginProcessor loginProcessor = beanManager.getReference(LoginProcessor.class);
        Dispatcher.post("/guide/next", loginProcessor::nextGuideStep, loginCheck::handle);
        Dispatcher.get("/guide", loginProcessor::showGuide, loginCheck::handle, csrfMidware::fill);
        Dispatcher.get("/login", loginProcessor::showLogin);
        Dispatcher.get("/forget-pwd", loginProcessor::showForgetPwd);
        Dispatcher.post("/forget-pwd", loginProcessor::forgetPwd, userForgetPwdValidationMidware::handle);
        Dispatcher.get("/reset-pwd", loginProcessor::showResetPwd);
        Dispatcher.post("/reset-pwd", loginProcessor::resetPwd);
        Dispatcher.get("/register", loginProcessor::showRegister);
        Dispatcher.post("/register", loginProcessor::register, userRegisterValidationMidware::handle);
        Dispatcher.post("/register2", loginProcessor::register2, userRegister2ValidationMidware::handle);
        Dispatcher.post("/login", loginProcessor::login);
        Dispatcher.get("/logout", loginProcessor::logout);
        Dispatcher.get("/githubLoginCallback", loginProcessor::githubLogin);
    }

    ExecutorService executorService = Executors.newCachedThreadPool();

    public static final ThreadPoolExecutor EXECUTOR_SERVICE = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public void githubLogin(final RequestContext context) {
        final Request request = context.getRequest();
        final Response response = context.getResponse();
        if (null != request.getAttribute(Common.CURRENT_USER)) {
            context.sendRedirect(Latkes.getServePath());

            return;
        }

        context.renderJSON(403).renderMsg(langPropsService.get("loginFailLabel"));

        String code = context.getRequest().getParameter("code");
        String state = context.getRequest().getParameter("state");
        String gotoUrl = context.getRequest().getHeader("referer");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String, String> param = Maps.newHashMap();
        param.put("code", code);
        param.put("client_id", "603d830f3705501acc91");
        param.put("client_secret", "969a7a02b0d327feebdaa6be42c50f7783b602b1");
        param.put("state", state);
        RequestBody loginBody =
                RequestBody.create(JSON, new Gson().toJson(param));
        LOGGER.info("loginBody => " + loginBody);

        Map<String, Object> map = Maps.newHashMap();
        CompletionService<Map<String, Object>> service = new ExecutorCompletionService(executorService);

        List<Future<Map<String, Object>>> mapFutures = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            mapFutures.add(service.submit(new Callable<Map<String, Object>>() {
                @Override
                public Map<String, Object> call() throws Exception {
                    String token = null;
                    try (okhttp3.Response res = HttpUtils.httpPost("https://github.com/login/oauth/access_token", loginBody)) {
                        try {
                            if (null != res && res.isSuccessful() && null != res.body()) {
                                String resstring = null;

                                resstring = res.body().string();
                                token = resstring.split("&")[0]
                                        .split("=")[1];
                                LOGGER.info("token => " + token);

                                OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                                        .readTimeout(20000, TimeUnit.MILLISECONDS).build();
                                ;
                                okhttp3.Request req = new okhttp3.Request.Builder()
                                        .header("Authorization", "token " + token)
                                        .url("https://api.github.com/user")
                                        .build();
                                okhttp3.Response res2 = client.newCall(req).execute();
                                String res3 = res2.body().string();
                                LOGGER.warn("获取到的用户登陆信息:" + res3);
                                if (res3 == null || res3.equals("")) {
                                    context.sendRedirect(Latkes.getServePath());
                                    LOGGER.warn("没有拿到用户登陆信息:" + res3);
                                    return null;
                                }
                                Gson gson = new Gson();
                                return gson.fromJson(res3, Map.class);
                            } else {
                                return null;
                            }
                        } catch (IOException ex) {
                            LOGGER.error("用户登陆信息异常:" + ex);
                            return null;
                        }
                    }
                }
            }));
        }
        for (int j = 1; j <= 10; j++) {
            try {
                Future<Map<String, Object>> take = service.take();

                Map<String, Object> result = take.get(); // 这一行代码在这里不会阻塞，引入放入队列中的都是已经完成的任务
                if (null != result) {
                    map = result;
                    break;
                } else {
                    LOGGER.info("result为NULL");
                }
            } catch (Exception e) {
                LOGGER.error("用户登陆信息异常:" + e);
            }

        }
        LOGGER.info("登陆信息:{}", map);
        //String ret = HttpUtils.sendPost("https://github.com/login/oauth/access_token?client_id=603d830f3705501acc91&client_secret=969a7a02b0d327feebdaa6be42c50f7783b602b1&code=" + code + "&redirect_uri=" + Latkes.getServePath() +"/githubLoginCallback", null);
        //String token = ret.split("&")[0];
///{"login":"cbamls","id":12781382,"node_id":"MDQ6VXNlcjEyNzgxMzgy","avatar_url":"https://avatars1.githubusercontent.com/u/12781382?v=4","gravatar_id":"","url":"https://api.github.com/users/cbamls","html_url":"https://github.com/cbamls","followers_url":"https://api.github.com/users/cbamls/followers","following_url":"https://api.github.com/users/cbamls/following{/other_user}","gists_url":"https://api.github.com/users/cbamls/gists{/gist_id}","starred_url":"https://api.github.com/users/cbamls/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/cbamls/subscriptions","organizations_url":"https://api.github.com/users/cbamls/orgs","repos_url":"https://api.github.com/users/cbamls/repos","events_url":"https://api.github.com/users/cbamls/events{/privacy}","received_events_url":"https://api.github.com/users/cbamls/received_events","type":"User","site_admin":false,"name":"cbamls","company":"北京三块在线科技 ","blog":"www.6aiq.com","location":"望京","email":"88cbam@gmail.com","hireable":null,"bio":"www.liangshu.me","public_repos":50,"public_gists":3,"followers":20,"following":4,"created_at":"2015-06-07T04:39:42Z","updated_at":"2018-12-15T08:58:44Z"}
        // String userJson = HttpUtils.sendGet("https://api.github.com/user?" + token + "");

        String email = map.get("email") == null ? "" : map.get("email").toString();

        try {
            JSONObject user = null;
            if (StringUtils.isBlank(email)) {
                String userName = (String) map.get("login");
                user = userQueryService.getUserByName(userName);
            } else {
                user = userQueryService.getUserByEmail(email);
            }
            if (user == null) {
                String userAvatarURL = (String) map.get("avatar_url");
                String loginName = (String) map.get("login");
                String nickName = (String) map.get("name");
                String userUrl = (String) map.get("blog");
                if (StringUtils.isBlank(userUrl)) {
                    userUrl = (String) map.get("html_url");
                }
                JSONObject newUser = new JSONObject();
                if (StringUtils.isBlank(email)) {
                    email = loginName + "@6aiq.com";
                    newUser.put(User.USER_EMAIL, email);
                } else {
                    newUser.put(User.USER_EMAIL, email);
                }
                LOGGER.info("github登录用户的信息 => " + email + " " + userAvatarURL + " " + loginName + " " + userUrl);
                newUser.put(User.USER_NAME, loginName);
                newUser.put(UserExt.USER_NICKNAME, nickName);
                newUser.put(User.USER_EMAIL, email);
                newUser.put(UserExt.USER_POINT, 500);
                newUser.put(UserExt.USER_STATUS, 0);
                if (StringUtils.isNotBlank(userUrl)) {
                    newUser.put(User.USER_URL, userUrl);
                    newUser.put(UserExt.USER_INTRO, userUrl);
                }
                newUser.put("userAvatarURL", userAvatarURL);
                newUser.put(User.USER_PASSWORD, "");
                final Locale locale = Locales.getLocale();
                newUser.put(UserExt.USER_LANGUAGE, locale.getLanguage() + "_" + locale.getCountry());
                final String newUserId = userMgmtService.addUser(newUser);
                user = userQueryService.getUserByEmail(email);
            }
            final String token2 = Sessions.login(response, user.optString(Keys.OBJECT_ID), true);

            final String ip = Requests.getRemoteAddr(request);
            userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), ip, true, true);

            context.renderJSON(StatusCodes.SUCC);
            context.renderJSONValue(Keys.TOKEN, token2);
            String redirectUrl = Latkes.getServePath();
            if (gotoUrl != null) {
                if (gotoUrl.contains("goto")) {
                    String realGotoUrl = gotoUrl.substring(gotoUrl.indexOf("goto=") + 5);
                    String decodeRealGotoUrl = URLDecoder.decode(realGotoUrl);
                    redirectUrl = decodeRealGotoUrl;
                } else if (!gotoUrl.contains("github.com")) {
                    redirectUrl = URLDecoder.decode(gotoUrl);
                }
            }
            context.sendRedirect(redirectUrl);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Next guide step.
     *
     * @param context the specified context
     */
    public void nextGuideStep(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);

        JSONObject requestJSONObject;
        try {
            requestJSONObject = context.requestJSON();
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage());
            return;
        }

        JSONObject user = Sessions.getUser();
        final String userId = user.optString(Keys.OBJECT_ID);

        int step = requestJSONObject.optInt(UserExt.USER_GUIDE_STEP);

        if (UserExt.USER_GUIDE_STEP_STAR_PROJECT < step || UserExt.USER_GUIDE_STEP_FIN >= step) {
            step = UserExt.USER_GUIDE_STEP_FIN;
        }

        try {
            user = userQueryService.getUser(userId);
            user.put(UserExt.USER_GUIDE_STEP, step);
            userMgmtService.updateUser(userId, user);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Guide next step [" + step + "] failed", e);
            return;
        }

        context.renderJSON(StatusCodes.SUCC);
    }

    /**
     * Shows guide page.
     *
     * @param context the specified context
     */
    public void showGuide(final RequestContext context) {
        final JSONObject currentUser = Sessions.getUser();
        final int step = currentUser.optInt(UserExt.USER_GUIDE_STEP);
        if (UserExt.USER_GUIDE_STEP_FIN == step) {
            context.sendRedirect(Latkes.getServePath());
            return;
        }

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "verify/guide.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.CURRENT_USER, currentUser);

        final List<JSONObject> tags = tagQueryService.getTags(32);
        dataModel.put(Tag.TAGS, tags);

        final List<JSONObject> users = userQueryService.getNiceUsers(6);
        final Iterator<JSONObject> iterator = users.iterator();
        while (iterator.hasNext()) {
            final JSONObject user = iterator.next();
            if (user.optString(Keys.OBJECT_ID).equals(currentUser.optString(Keys.OBJECT_ID))) {
                iterator.remove();
                break;
            }
        }
        dataModel.put(User.USERS, users);

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Shows login page.
     *
     * @param context the specified context
     */
    public void showLogin(final RequestContext context) {
        if (Sessions.isLoggedIn()) {
            context.sendRedirect(Latkes.getServePath());
            return;
        }

        String referer = context.param(Common.GOTO);
        if (StringUtils.isBlank(referer)) {
            referer = context.header("referer");
        }

        if (!StringUtils.startsWith(referer, Latkes.getServePath())) {
            referer = Latkes.getServePath();
        }

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "verify/login.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.GOTO, URLs.encode(referer));

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Shows forget password page.
     *
     * @param context the specified context
     */
    public void showForgetPwd(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "verify/forget-pwd.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Forget password.
     *
     * @param context the specified context
     */
    public void forgetPwd(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);

        final JSONObject requestJSONObject = (JSONObject) context.attr(Keys.REQUEST);
        final String email = requestJSONObject.optString(User.USER_EMAIL);

        try {
            final JSONObject user = userQueryService.getUserByEmail(email);
            if (null == user || UserExt.USER_STATUS_C_VALID != user.optInt(UserExt.USER_STATUS)) {
                context.renderMsg(langPropsService.get("notFoundUserLabel"));
                return;
            }

            final String userId = user.optString(Keys.OBJECT_ID);

            final JSONObject verifycode = new JSONObject();
            verifycode.put(Verifycode.BIZ_TYPE, Verifycode.BIZ_TYPE_C_RESET_PWD);
            final String code = RandomStringUtils.randomAlphanumeric(6);
            verifycode.put(Verifycode.CODE, code);
            verifycode.put(Verifycode.EXPIRED, DateUtils.addDays(new Date(), 1).getTime());
            verifycode.put(Verifycode.RECEIVER, email);
            verifycode.put(Verifycode.STATUS, Verifycode.STATUS_C_UNSENT);
            verifycode.put(Verifycode.TYPE, Verifycode.TYPE_C_EMAIL);
            verifycode.put(Verifycode.USER_ID, userId);
            verifycodeMgmtService.addVerifycode(verifycode);

            context.renderJSON(StatusCodes.SUCC).renderMsg(langPropsService.get("verifycodeSentLabel"));
        } catch (final ServiceException e) {
            final String msg = langPropsService.get("resetPwdLabel") + " - " + e.getMessage();
            LOGGER.log(Level.ERROR, msg + "[email=" + email + "]");
            context.renderMsg(msg);
        }
    }

    /**
     * Shows reset password page.
     *
     * @param context the specified context
     */
    public void showResetPwd(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, null);
        context.setRenderer(renderer);
        final Map<String, Object> dataModel = renderer.getDataModel();

        final String code = context.param("code");
        final JSONObject verifycode = verifycodeQueryService.getVerifycode(code);
        if (null == verifycode) {
            dataModel.put(Keys.MSG, langPropsService.get("verifycodeExpiredLabel"));
            renderer.setTemplateName("error/custom.ftl");
        } else {
            renderer.setTemplateName("verify/reset-pwd.ftl");

            final String userId = verifycode.optString(Verifycode.USER_ID);
            final JSONObject user = userQueryService.getUser(userId);
            dataModel.put(User.USER, user);
            dataModel.put(Keys.CODE, code);
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Resets password.
     *
     * @param context the specified context
     */
    public void resetPwd(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);

        final Response response = context.getResponse();
        final JSONObject requestJSONObject = context.requestJSON();
        final String password = requestJSONObject.optString(User.USER_PASSWORD); // Hashed
        final String userId = requestJSONObject.optString(UserExt.USER_T_ID);
        final String code = requestJSONObject.optString(Keys.CODE);
        final JSONObject verifycode = verifycodeQueryService.getVerifycode(code);
        if (null == verifycode || !verifycode.optString(Verifycode.USER_ID).equals(userId)) {
            context.renderMsg(langPropsService.get("verifycodeExpiredLabel"));
            return;
        }

        String name = null;
        String email = null;
        try {
            final JSONObject user = userQueryService.getUser(userId);
            if (null == user || UserExt.USER_STATUS_C_VALID != user.optInt(UserExt.USER_STATUS)) {
                context.renderMsg(langPropsService.get("resetPwdLabel") + " - " + "User Not Found");
                return;
            }

            user.put(User.USER_PASSWORD, password);
            userMgmtService.updatePassword(user);
            verifycodeMgmtService.removeByCode(code);
            context.renderJSON(StatusCodes.SUCC);
            LOGGER.info("User [email=" + user.optString(User.USER_EMAIL) + "] reseted password");
            Sessions.login(response, userId, true);
        } catch (final ServiceException e) {
            final String msg = langPropsService.get("resetPwdLabel") + " - " + e.getMessage();
            LOGGER.log(Level.ERROR, msg + "[name={}, email={}]", name, email);
            context.renderMsg(msg);
        }
    }

    /**
     * Shows registration page.
     *
     * @param context the specified context
     */
    public void showRegister(final RequestContext context) {
        if (Sessions.isLoggedIn()) {
            context.sendRedirect(Latkes.getServePath());
            return;
        }

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, null);
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.REFERRAL, "");

        boolean useInvitationLink = false;

        String referral = context.param("r");
        if (!UserRegisterValidationMidware.invalidUserName(referral)) {
            final JSONObject referralUser = userQueryService.getUserByName(referral);
            if (null != referralUser) {
                dataModel.put(Common.REFERRAL, referral);

                final Map<String, JSONObject> permissions =
                        roleQueryService.getUserPermissionsGrantMap(referralUser.optString(Keys.OBJECT_ID));
                final JSONObject useILPermission =
                        permissions.get(Permission.PERMISSION_ID_C_COMMON_USE_INVITATION_LINK);
                useInvitationLink = useILPermission.optBoolean(Permission.PERMISSION_T_GRANT);
            }
        }

        final String code = context.param("code");
        if (StringUtils.isBlank(code)) { // Register Step 1
            renderer.setTemplateName("verify/register.ftl");
        } else { // Register Step 2
            final JSONObject verifycode = verifycodeQueryService.getVerifycode(code);
            if (null == verifycode) {
                dataModel.put(Keys.MSG, langPropsService.get("verifycodeExpiredLabel"));
                renderer.setTemplateName("error/custom.ftl");
            } else {
                renderer.setTemplateName("verify/register2.ftl");

                final String userId = verifycode.optString(Verifycode.USER_ID);
                final JSONObject user = userQueryService.getUser(userId);
                dataModel.put(User.USER, user);

                if (UserExt.USER_STATUS_C_VALID == user.optInt(UserExt.USER_STATUS)
                        || UserExt.NULL_USER_NAME.equals(user.optString(User.USER_NAME))) {
                    dataModel.put(Keys.MSG, langPropsService.get("userExistLabel"));
                    renderer.setTemplateName("error/custom.ftl");
                } else {
                    referral = StringUtils.substringAfter(code, "r=");
                    if (StringUtils.isNotBlank(referral)) {
                        dataModel.put(Common.REFERRAL, referral);
                    }
                }
            }
        }

        final String allowRegister = optionQueryService.getAllowRegister();
        dataModel.put(Option.ID_C_MISC_ALLOW_REGISTER, allowRegister);
        if (useInvitationLink && "2".equals(allowRegister)) {
            dataModel.put(Option.ID_C_MISC_ALLOW_REGISTER, "1");
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Register Step 1.
     *
     * @param context the specified context
     */
    public void register(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);
        final JSONObject requestJSONObject = context.getRequest().getJSON();
        final String name = requestJSONObject.optString(User.USER_NAME);
        final String email = requestJSONObject.optString(User.USER_EMAIL);
        final String invitecode = requestJSONObject.optString(Invitecode.INVITECODE);
        final String referral = requestJSONObject.optString(Common.REFERRAL);

        final JSONObject user = new JSONObject();
        user.put(User.USER_NAME, name);
        user.put(User.USER_EMAIL, email);
        user.put(User.USER_PASSWORD, "");
        final Locale locale = Locales.getLocale();
        user.put(UserExt.USER_LANGUAGE, locale.getLanguage() + "_" + locale.getCountry());

        try {
            final String newUserId = userMgmtService.addUser(user);

            final JSONObject verifycode = new JSONObject();
            verifycode.put(Verifycode.BIZ_TYPE, Verifycode.BIZ_TYPE_C_REGISTER);
            String code = RandomStringUtils.randomAlphanumeric(6);
            if (StringUtils.isNotBlank(referral)) {
                code += "r=" + referral;
            }
            verifycode.put(Verifycode.CODE, code);
            verifycode.put(Verifycode.EXPIRED, DateUtils.addDays(new Date(), 1).getTime());
            verifycode.put(Verifycode.RECEIVER, email);
            verifycode.put(Verifycode.STATUS, Verifycode.STATUS_C_UNSENT);
            verifycode.put(Verifycode.TYPE, Verifycode.TYPE_C_EMAIL);
            verifycode.put(Verifycode.USER_ID, newUserId);
            verifycodeMgmtService.addVerifycode(verifycode);

            final String allowRegister = optionQueryService.getAllowRegister();
            if ("2".equals(allowRegister) && StringUtils.isNotBlank(invitecode)) {
                final JSONObject ic = invitecodeQueryService.getInvitecode(invitecode);
                ic.put(Invitecode.USER_ID, newUserId);
                ic.put(Invitecode.USE_TIME, System.currentTimeMillis());
                final String icId = ic.optString(Keys.OBJECT_ID);

                invitecodeMgmtService.updateInvitecode(icId, ic);
            }

            context.renderJSON(StatusCodes.SUCC).renderMsg(langPropsService.get("verifycodeSentLabel"));
        } catch (final ServiceException e) {
            final String msg = langPropsService.get("registerFailLabel") + " - " + e.getMessage();
            LOGGER.log(Level.ERROR, msg + "[name={}, email={}]", name, email);
            context.renderMsg(msg);
        }
    }

    /**
     * Register Step 2.
     *
     * @param context the specified context
     */
    public void register2(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR);

        final Request request = context.getRequest();
        final Response response = context.getResponse();
        final JSONObject requestJSONObject = context.getRequest().getJSON();

        final String password = requestJSONObject.optString(User.USER_PASSWORD); // Hashed
        final int appRole = requestJSONObject.optInt(UserExt.USER_APP_ROLE);
        final String referral = requestJSONObject.optString(Common.REFERRAL);
        final String userId = requestJSONObject.optString(UserExt.USER_T_ID);

        String name = null;
        String email = null;
        try {
            final JSONObject user = userQueryService.getUser(userId);
            if (null == user) {
                context.renderMsg(langPropsService.get("registerFailLabel") + " - " + "User Not Found");
                return;
            }

            name = user.optString(User.USER_NAME);
            email = user.optString(User.USER_EMAIL);

            user.put(UserExt.USER_APP_ROLE, appRole);
            user.put(User.USER_PASSWORD, password);
            user.put(UserExt.USER_STATUS, UserExt.USER_STATUS_C_VALID);

            userMgmtService.addUser(user);

            Sessions.login(response, userId, false);

            final String ip = Requests.getRemoteAddr(request);
            userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), ip, true, true);

            if (StringUtils.isNotBlank(referral) && !UserRegisterValidationMidware.invalidUserName(referral)) {
                final JSONObject referralUser = userQueryService.getUserByName(referral);
                if (null != referralUser) {
                    final String referralId = referralUser.optString(Keys.OBJECT_ID);
                    pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, userId,
                            Pointtransfer.TRANSFER_TYPE_C_INVITED_REGISTER,
                            Pointtransfer.TRANSFER_SUM_C_INVITE_REGISTER, referralId, System.currentTimeMillis(), "");
                    pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, referralId,
                            Pointtransfer.TRANSFER_TYPE_C_INVITE_REGISTER,
                            Pointtransfer.TRANSFER_SUM_C_INVITE_REGISTER, userId, System.currentTimeMillis(), "");

                    final JSONObject notification = new JSONObject();
                    notification.put(Notification.NOTIFICATION_USER_ID, referralId);
                    notification.put(Notification.NOTIFICATION_DATA_ID, userId);
                    notificationMgmtService.addInvitationLinkUsedNotification(notification);
                }
            }

            final JSONObject ic = invitecodeQueryService.getInvitecodeByUserId(userId);
            if (null != ic && Invitecode.STATUS_C_UNUSED == ic.optInt(Invitecode.STATUS)) {
                ic.put(Invitecode.STATUS, Invitecode.STATUS_C_USED);
                ic.put(Invitecode.USER_ID, userId);
                ic.put(Invitecode.USE_TIME, System.currentTimeMillis());
                final String icId = ic.optString(Keys.OBJECT_ID);

                invitecodeMgmtService.updateInvitecode(icId, ic);

                final String icGeneratorId = ic.optString(Invitecode.GENERATOR_ID);
                if (StringUtils.isNotBlank(icGeneratorId) && !Pointtransfer.ID_C_SYS.equals(icGeneratorId)) {
                    pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, icGeneratorId,
                            Pointtransfer.TRANSFER_TYPE_C_INVITECODE_USED,
                            Pointtransfer.TRANSFER_SUM_C_INVITECODE_USED, userId, System.currentTimeMillis(), "");

                    final JSONObject notification = new JSONObject();
                    notification.put(Notification.NOTIFICATION_USER_ID, icGeneratorId);
                    notification.put(Notification.NOTIFICATION_DATA_ID, userId);

                    notificationMgmtService.addInvitecodeUsedNotification(notification);
                }
            }

            context.renderJSON(StatusCodes.SUCC);

            LOGGER.log(Level.INFO, "Registered a user [name={}, email={}]", name, email);
        } catch (final ServiceException e) {
            final String msg = langPropsService.get("registerFailLabel") + " - " + e.getMessage();
            LOGGER.log(Level.ERROR, msg + " [name={}, email={}]", name, email);
            context.renderMsg(msg);
        }
    }

    /**
     * Logins user.
     *
     * @param context the specified context
     */
    public void login(final RequestContext context) {
        final Request request = context.getRequest();
        final Response response = context.getResponse();
        context.renderJSON(StatusCodes.ERR).renderMsg(langPropsService.get("loginFailLabel"));
        final JSONObject requestJSONObject = context.requestJSON();
        final String nameOrEmail = requestJSONObject.optString("nameOrEmail");

        try {
            JSONObject user = userQueryService.getUserByName(nameOrEmail);
            if (null == user) {
                user = userQueryService.getUserByEmail(nameOrEmail);
            }

            if (null == user) {
                context.renderMsg(langPropsService.get("notFoundUserLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_INVALID == user.optInt(UserExt.USER_STATUS)) {
                userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), "", false, true);
                context.renderMsg(langPropsService.get("userBlockLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_NOT_VERIFIED == user.optInt(UserExt.USER_STATUS)) {
                userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), "", false, true);
                context.renderMsg(langPropsService.get("notVerifiedLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_INVALID_LOGIN == user.optInt(UserExt.USER_STATUS)
                    || UserExt.USER_STATUS_C_DEACTIVATED == user.optInt(UserExt.USER_STATUS)) {
                userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), "", false, true);
                context.renderMsg(langPropsService.get("invalidLoginLabel"));
                return;
            }

            final String userId = user.optString(Keys.OBJECT_ID);
            JSONObject wrong = WRONG_PWD_TRIES.get(userId);
            if (null == wrong) {
                wrong = new JSONObject();
            }

            final int wrongCount = wrong.optInt(Common.WRON_COUNT);
            if (wrongCount > 3) {
                final String captcha = requestJSONObject.optString(CaptchaProcessor.CAPTCHA);
                if (!StringUtils.equals(wrong.optString(CaptchaProcessor.CAPTCHA), captcha)) {
                    context.renderMsg(langPropsService.get("captchaErrorLabel"));
                    context.renderJSONValue(Common.NEED_CAPTCHA, userId);
                    return;
                }
            }

            final String userPassword = user.optString(User.USER_PASSWORD);
            if (userPassword.equals(requestJSONObject.optString(User.USER_PASSWORD))) {
                final String token = Sessions.login(response, userId, requestJSONObject.optBoolean(Common.REMEMBER_LOGIN));

                final String ip = Requests.getRemoteAddr(request);
                userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), ip, true, true);

                context.renderCodeMsg(StatusCodes.SUCC, "");
                context.renderJSONValue(Keys.TOKEN, token);

                WRONG_PWD_TRIES.remove(userId);
                return;
            }

            if (wrongCount > 2) {
                context.renderJSONValue(Common.NEED_CAPTCHA, userId);
            }

            wrong.put(Common.WRON_COUNT, wrongCount + 1);
            WRONG_PWD_TRIES.put(userId, wrong);

            context.renderMsg(langPropsService.get("wrongPwdLabel"));
        } catch (final ServiceException e) {
            context.renderMsg(langPropsService.get("loginFailLabel"));
        }
    }

    /**
     * Logout.
     *
     * @param context the specified context
     */
    public void logout(final RequestContext context) {
        final JSONObject user = Sessions.getUser();
        if (null != user) {
            Sessions.logout(user.optString(Keys.OBJECT_ID), context.getResponse());
        }

        String destinationURL = context.param(Common.GOTO);
        if (StringUtils.isBlank(destinationURL)) {
            destinationURL = context.header("referer");
        }

        if (!StringUtils.startsWith(destinationURL, Latkes.getServePath())) {
            destinationURL = Latkes.getServePath();
        }

        context.sendRedirect(destinationURL);
    }
}
