<#--

    Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
    Copyright (C) 2012-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#include "macro-head.ftl">
<#include "common/title-icon.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="AIQ - 全国最专业的人工智能技术社区">
        <meta name="description" content="${symDescriptionLabel}"/>
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}"/>
    <link rel="canonical" href="${servePath}">
    <link type="text/css" href="${staticServePath}/css/lunbo.css" rel="stylesheet"/>
</head>
<body class="index">
${HeaderBannerLabel}
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="index-main">
           <a href="https://github.com/cbamls/AI_Tutorial"><img style="position: absolute; top: 40px; left: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_left_red_aa0000.png" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_left_red_aa0000.png"></a>

            <div id="zSlider">
                <div id="picshow">
                    <div id="picshow_img">
                        <ul>
                            <#list perfectArticles as article>
                                <#if (article_index < 7) && article.articleThumbnailURLBig??>
                                    <li>
                                        <a href="${servePath}${article.articlePermalink}">
                                            <img style="width: 100%" src="${article.articleThumbnailURLBig}">
                                            <#--                                            <img style="width: 100%" src="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1582649332280&di=c24388d4d757cab26619282c2fd0e86f&imgtype=0&src=http%3A%2F%2Ffile02.16sucai.com%2Fd%2Ffile%2F2014%2F0704%2Fe53c868ee9e8e7b28c424b56afe2066d.jpg">-->
                                        </a>
                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </div>

                    <div id="picshow_tx">
                        <ul>
                            <#list perfectArticles as article>
                                <#if (article_index < 7) && article.articleThumbnailURLBig??>
                                    <li>
                                        <h3><a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                                <span class="avatar-small tooltipped tooltipped-se"
                                                      aria-label="${article.articleAuthorName}"
                                                      style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                                            </a>
                                            <a rel="nofollow" class="fn-ellipsis ft-a-title"
                                               href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                            <a class="fn-right count ft-gray ft-smaller"
                                               href="${servePath}${article.articlePermalink}"><span
                                                        style="color: red"><#if article.articleViewCount < 1000>
                                    ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></span></a>
                                        </h3>
                                        <p> ${article.articlePreviewContent}</p>

                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </div>
                </div>

            </div>
            <div class="index-tabs fn-flex" id="articles">
                        <span class="current" data-index="0">
                        </span>
                <span class="tags" data-index="1">
                        </span>
                <span class="users" data-index="2">
                        </span>
            </div>
            <div class="index-tabs-panels list article-list">
                <ul>
                    <#list recentArticles as article>
                    <#if (article_index == 1 || article_index % 3 == 1)>
                    <script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-1229303764778930"
                         crossorigin="anonymous"></script>
                    <ins class="adsbygoogle"
                         style="display:block"
                         data-ad-format="fluid"
                         data-ad-layout-key="-f2-g-2w-jc+1jn"
                         data-ad-client="ca-pub-1229303764778930"
                         data-ad-slot="4649425285"></ins>
                    <script>
                         (adsbygoogle = window.adsbygoogle || []).push({});
                    </script>
                    </#if>
                        <#include "common/list-item.ftl">
                    </#list>
                    <#if recentArticles?size == 0>
                        <li class="ft-center">
                            <br><br>
                            ${systemEmptyLabel}<br>
                            ${systemEmptyTipLabel}<br>
                            <img src="${staticServePath}/images/404/5.gif"/>
                        </li>
                    </#if>
                    <li>
                        <a class="more" href="${servePath}/recent">${moreRecentArticleLabel}</a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="index-side">
            <#if !isLoggedIn>
                <div style="background-color: rgba(98,162,240,.1);width: 100%;box-sizing: border-box;">
                    <#if !isLoggedIn>
                        <div style="background-color: rgba(98,162,240,.1);width: 100%;box-sizing: border-box;">
                            <#include 'common/person-info.ftl'/>
                            <div class="module" style="padding-bottom: 5px; margin-bottom: 0px">
                                <div class="module-header">
                                    <h2>当前在线: ${visitors}， 刚加入的小伙伴:</h2>
                                </div>
                                <div class="module-panel index__user-panel" style="margin-left: 40px">
                                    <#list users as user>
                                        <a class="avatar index__user-avatar tooltipped tooltipped-se"
                                           href="https://www.6aiq.com/member/${user.userName}" aria-label="${user.userName}"
                                           style="background-image: url(&quot;${user.userAvatarURL48}&quot;);"></a>
                                    </#list>
                                    <span class="avatar index__user-avatar"> <h2 style="text-align: center">....</h2></span>
                                </div>
                            </div>
                            <div class="fn__flex-1 bg"></div>
                        </div>
                    </#if>
                    <div class="fn__flex-1 bg"></div>
                </div>
            </#if>

            <#if isLoggedIn>
             <#include 'common/person-info.ftl'/>
            </#if>
            <div class="index-tabs fn-flex">
                        <span class="perfect">
                        </span>
                <span class="check">
                        </span>
            </div>
            <div class="perfect-panel list">
                <ul>
                    <#list perfectArticles as article>
                        <#if (article_index >= 7)>
                            <li>
                                <#if article.articleAuthorName?? && article.articleAuthorName !="">
                                    <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL48}')"></span>
                                    </a>
                                    <a rel="nofollow" class="fn-ellipsis ft-a-title"
                                       href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                    <a class="fn-right count ft-gray ft-smaller"
                                       href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                            ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                                <#else>
                                    异常渲染${article}
                                </#if>
                            </li>
                        </#if>
                    </#list>
                    <#if perfectArticles?size == 0>
                        <li>${chickenEggLabel}</li>
                    </#if>
                </ul>
            </div>
            <#if ADLabel!="" && ((tag?? && tag.tagShowSideAd == 0) || !tag??)>
            <div class="module">
                <div class="module-header">
                    <h2>
                        ${sponsorLabel}
                        <a href="${servePath}/about" class="fn-right ft-13 ft-gray" target="_blank">${wantPutOnLabel}</a>
                    </h2>
                </div>
                <div class="module-panel ad fn-clear">
                    ${ADLabel}
                </div>
            </div>
            </#if>
        </div>
    </div>
</div>
<div class="index__bottom">
    <div class="wrapper">
        <div class="fn-flex-1">
            <div class="metro-line fn-flex">
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag0.tagURI}">
                        <img src="${tag0.tagIconPath}" alt="${tag0.tagTitle}">
                        <b>${tag0.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag1.tagURI}">
                        <img src="${tag1.tagIconPath}" alt="${tag1.tagTitle}">
                        <b>${tag1.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag2.tagURI}">
                        <img src="${tag2.tagIconPath}" alt="${tag2.tagTitle}">
                        <b>${tag2.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag3.tagURI}">
                        <img src="${tag3.tagIconPath}" alt="${tag3.tagTitle}">
                        <b>${tag3.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag4.tagURI}">
                        <img src="${tag4.tagIconPath}" alt="${tag4.tagTitle}">
                        <b>${tag4.tagTitle}</b>
                    </a>
                </div>
            </div>
            <div class="metro-line fn-flex">
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag5.tagURI}">
                        <img src="${tag5.tagIconPath}" alt="${tag5.tagTitle}">
                        <b>${tag5.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag6.tagURI}">
                        <img src="${tag6.tagIconPath}" alt="${tag6.tagTitle}">
                        <b>${tag6.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag7.tagURI}">
                        <img src="${tag7.tagIconPath}" alt="${tag7.tagTitle}">
                        <b>${tag7.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag8.tagURI}">
                        <img src="${tag8.tagIconPath}" alt="${tag8.tagTitle}">
                        <b>${tag8.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag9.tagURI}">
                        <img src="${tag9.tagIconPath}" alt="${tag9.tagTitle}">
                        <b>${tag9.tagTitle}</b>
                    </a>
                </div>
            </div>
            <div class="metro-line fn-flex">
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag10.tagURI}">
                        <img src="${tag10.tagIconPath}" alt="${tag10.tagTitle}">
                        <b>${tag10.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag11.tagURI}">
                        <img src="${tag11.tagIconPath}" alt="${tag11.tagTitle}">
                        <b>${tag11.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <a class="preview" href="${servePath}/tag/${tag12.tagURI}">
                        <img src="${tag12.tagIconPath}" alt="${tag12.tagTitle}">
                        <b>${tag12.tagTitle}</b>
                    </a>
                </div>
                <div class="metro-item">
                    <#if ADLabel != '' >
                        <a class="preview" href="${servePath}/about">
                            <img src="${staticServePath}/emoji/graphics/heart.png" alt="${sponsorLabel}">
                            <b>${adDeliveryLabel}</b>
                        </a>
                    <#else>
                        <a class="preview" href="${servePath}/about">
                            <img src="${staticServePath}/emoji/graphics/heart.png" alt="${sponsorLabel}">
                            <b>${adDeliveryLabel}</b>
                        </a>
                    </#if>
                </div>
                <div class="metro-item">
                    <#if ADLabel != '' >
                        <div class="ad">
                            ${ADLabel}
                        </div>
                    <#else>
                        <a class="preview" href="${servePath}/about">
                            <img src="${staticServePath}/emoji/graphics/heart.png" alt="${sponsorLabel}">
                            <b>${adDeliveryLabel}</b>
                        </a>
                    </#if>
                </div>
            </div>

            <div class="metro-border fn-flex">
                <div></div>
                <div class="green"></div>
                <div class="yellow"></div>
                <div class="red"></div>
                <div class="purple"></div>
            </div>
        </div>
    </div>
</div>
</div>

<#include "footer.ftl">
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
<script type="text/javascript">
    $('.metro-item').height($('.metro-item').width());

    // tag click
    $('.preview, .index-tabs > span').click(function (event) {
        var $it = $(this),
            maxLen = Math.max($it.width(), $it.height());
        $it.prepend('<span class="ripple" style="top: ' + (event.offsetY - $it.height() / 2)
            + 'px;left:' + (event.offsetX - $it.width() / 2) + 'px;height:' + maxLen + 'px;width:' + maxLen + 'px"></span>');

        setTimeout(function () {
            $it.find('.ripple').remove();
        }, 800);
    });
</script>
<script src="https://www.jq22.com/jquery/jquery-1.10.2.js"></script>

<script type="text/javascript" src="${staticServePath}/js/lunbo.js"></script>
</body>
</html>
