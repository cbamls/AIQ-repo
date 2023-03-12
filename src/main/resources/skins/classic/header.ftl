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
<style>

.home-right-item-wrap.hot-article {
    padding: 0;
    --tw-bg-opacity: 1;
    background-color: rgba(255, 255, 255, var(--tw-bg-opacity));
}
.home-right-item-wrap {
    background-color: var(--pai-bg-white-fff);
    overflow: hidden;
    padding: 20px;
    margin-bottom: 20px;
}
.hot-article-bg {
    height: 74px;
    margin-top: -14px;
}

.w-full {
    width: 100%;
}
.hot-article-content {
    padding: 20px;
    margin-top: -70px;
}
.home-right-item-title {
    margin-bottom: 24px;
    left: 20px;
    top: 16px;
}
.home-right-item-wrap .com-2-panel-title {
    font-size: 18px;
}
.home-right-item-article-item, .home-right-item-post-item {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    color: #616161;
    justify-content: space-between;
}
.hot-article
    .home-right-item-article-list
    .home-right-item-article-item:nth-child(1)
    i {
    background: #f85959;
  }

  .hot-article
    .home-right-item-article-list
    .home-right-item-article-item:nth-child(2)
    i {
    background: #fe6617;
  }

  .hot-article
    .home-right-item-article-list
    .home-right-item-article-item:nth-child(3)
    i {
    background: #f59e2f;
  }

  .hot-article .home-right-item-article-list .home-right-item-article-item i {
    height: 18px;
    width: 18px;
    line-height: 18px;
    background: #ccd0d7;
    color: #fff;
    display: inline-block;
    text-align: center;
  }
    .nav .nav-tabs svg {
        margin: 3px 2px 0 0;
        float: left;
        visibility: visible;
    }
</style>

<div class="nav">
    <h1 aria-label="${symphonyLabel}" class="tooltipped tooltipped-s">
        <a href="${servePath}" style="outline: 0;color: black; text-decoration: none;">
            <img style="height: 40px;margin-top:4px" src="${servePath}/images/logo.png" alt="人工智能"/>
            <img style="margin-top:4px" src="${servePath}/images/aiq.png" alt="人工智能"/>
        </a>

    </h1>
    <div class="nav-tabs">
    <a pjax-title="实时周报 - ${symphonyLabel}"
                       href="${servePath}/links"<#if selected?? && 'links' == selected> class="current"</#if>>
                       <img src="https://hiphotos.baidu.com/feed/pic/item/0bd162d9f2d3572c903430e98713632762d0c335.jpg" style="height: 20px";>  </img><b>实时周报</b></a>

        <#list domains as domain>
            <a pjax-title="${domain.domainTitle} - ${domainLabel} - ${symphonyLabel}"
               href="${servePath}/domain/${domain.domainURI}"<#if selected?? && selected == domain.domainURI>
                class="current"</#if>>${domain.domainIconPath} ${domain.domainTitle}</a>
        </#list>
         <a pjax-title="${latestLabel} - ${symphonyLabel}"
           href="${servePath}/recent"<#if selected?? && 'recent' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#refresh"></use>
            </svg> ${latestLabel}</a>
        <a pjax-title="${qnaLabel} - ${symphonyLabel}"
           href="${servePath}/qna"<#if selected?? && 'qna' == selected> </#if>>
            <svg>
                <use xlink:href="#iconAsk"></use>
            </svg> ${qnaLabel}</a>
        <a href="${servePath}/perfect"<#if selected?? && 'perfect' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#perfect"></use>
            </svg> ${perfectLabel}</a>
<#--        <a href="http://www.md6s.com/" target="_blank">-->
<#--            <strong style="color: red">Markdown编辑器</strong>-->
<#--        </a>-->

        <#if isLoggedIn && "" != currentUser.userCity>
            <a href="${servePath}/city/my"<#if selected?? && 'city' == selected> class="current"</#if>>
                <svg>
                    <use xlink:href="#local"></use>
                </svg> ${currentUser.userCity}</a>
        </#if>
        <#if isLoggedIn>
            <a href="${servePath}/watch"<#if selected?? && 'watch' == selected> class="current"</#if>>
                <svg>
                    <use xlink:href="#view"></use>
                </svg> ${followLabel}</a>
        </#if>
    </div>
    <#if esEnabled || algoliaEnabled>
        <form class="responsive-hide fn-left" target="_blank" action="/search">
            <input class="search" style="    width: 250px;
                                             height: 30px;" placeholder="智能搜索..." type="text" name="key" id="search"
                   value="<#if key??>${key}</#if>">

            <input type="submit" class="fn-none" value=""/>
        </form>
    </#if>
    <div class="user-nav">
        <#if isLoggedIn>
            <a href="${servePath}/pre-post" class="tooltipped tooltipped-w" aria-label="${postArticleLabel}">
                <svg>
                    <use xlink:href="#addfile"></use>
                </svg>
            </a>
            <#if permissions["menuAdmin"].permissionGrant>
                <a href="${servePath}/admin" aria-label="${adminLabel}" class="tooltipped tooltipped-w">
                    <svg>
                        <use xlink:href="#userrole"></use>
                    </svg>
                </a>
            </#if>
            <a id="aNotifications"
               class="tooltipped tooltipped-w <#if unreadNotificationCount == 0>no-msg<#else>msg</#if>"
               href="${servePath}/notifications" aria-label="${messageLabel}">${unreadNotificationCount}</a>
            <a href="${servePath}/activities" aria-label="${activityLabel}" class="tooltipped tooltipped-w">
                <svg>
                    <use xlink:href="#flag"></use>
                </svg>
            </a>
            <a href="javascript:void(0)" id="aPersonListPanel" class="tooltipped tooltipped-w"
               aria-label="${viewHomeAndProfileLabel}"
               data-url="${servePath}/member/${currentUser.userName}">
                <span class="avatar-small" style="background-image:url('${currentUser.userAvatarURL20}')"></span>
            </a>
            <div class="module person-list" id="personListPanel">
                <ul>
                    <li>
                        <a href="${servePath}/member/${currentUser.userName}">${goHomeLabel}</a>
                    </li>
                    <li>
                        <a href="${servePath}/settings">${settingsLabel}</a>
                    </li>
                    <li>
                        <a href="${servePath}/settings/help">${helpLabel}</a>
                    </li>
                    <li>
                        <a href="javascript:Util.logout()">${logoutLabel}</a>
                    </li>
                </ul>
            </div>
        <#else>
            <a href="javascript: Util.goLogin();" class="unlogin">${loginLabel}</a>
            <a href="javascript:Util.goRegister()" class="unlogin">${registerLabel}</a>
        </#if>
         <a href="https://support.qq.com/products/416808" target="_blank">
                    反馈&建议
                </a>
    </div>
</div>
