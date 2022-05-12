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
    <#if !isLoggedIn>
    <div class="responsive-hide" style="background-color: rgba(98,162,240,.1);width: 100%;box-sizing: border-box; ">
        <div style="-webkit-box-flex: 1;flex: 2;min-width: 1px;padding: 50px;margin-bottom: 0px; padding-bottom: 20px;display: flex;align-items: center;background: url(https://cdn-static.aijishu.com/v-5f2b6cd0/home-banner.1ac8c533.svg) no-repeat 100% 100%">
            <div style="padding-right: 8%; margin-left: 3%">
                <h2 style="font-size: 1.75rem">
                    欢迎来到 <strong>AIQ</strong> - 专业的人工智能技术社区
                </h2>
                <br>

                <div class="ft-gray" style="font-size: 1.12rem;">
                    欢迎加入AIQ，与 22000+ 人工智能算法爱好者共同书写中国人工智能新篇章。在这里会分享人工智能在企业落地上的一些实践、AI智能领域的资讯、知识及教育培训、会议活动；鼓励技术写作和问答互助。大家在这里相互信任，以平等 • 自由 • 奔放的价值观进行分享交流。
                </div>
            </div>


            <div style="width: 100%">
                <a href="https://www.6aiq.com/register" class="btn green" style="font-size: 1.25rem;
    padding: 10px;">免费注册</a>
             <!--   <a onclick="window.location='https://github.com/login/oauth/authorize?client_id=603d830f3705501acc91&redirect_uri=${servePath}/githubLoginCallback2&scope=user&state=3'"
                   class="btn green" style="font-size: 1.25rem;
    padding: 10px;">
                    Github登陆
                    <svg class="unlogin">
                        <use xlink:href="#github"></use>
                    </svg>
                </a>
                &nbsp; -->
                 <iframe src="https://ghbtns.com/github-btn.html?user=cbamls&repo=AI_Tutorial&type=star&count=true&size=large" frameborder="0" scrolling="0" width="170" height="30" title="GitHub"></iframe>

                <a href="https://github.com/cbamls/AI_Tutorial"><img  src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png"></a>
            </div>
        </div>
        <div class="fn__flex-1 bg"></div>
    </div>
</#if>