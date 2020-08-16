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


            <div style="width: 58%">
                <a href="https://www.6aiq.com/register" class="btn green" style="font-size: 1.25rem;
    padding: 10px;">免费注册</a>
                <a onclick="window.location='https://github.com/login/oauth/authorize?client_id=603d830f3705501acc91&redirect_uri=${servePath}/githubLoginCallback&scope=user&state=3'"
                   class="btn green" style="font-size: 1.25rem;
    padding: 10px;">
                    快速登陆
                    <svg class="unlogin">
                        <use xlink:href="#github"></use>
                    </svg>
                </a>&nbsp;
            </div>
        </div>
        <div class="fn__flex-1 bg"></div>
    </div>
</#if>