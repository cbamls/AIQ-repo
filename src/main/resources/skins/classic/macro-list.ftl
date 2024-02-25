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
<#macro list listData>
<#include "common/title-icon.ftl">
<div class="article-list list">
    <ul>
        <#assign articleIds = "">
        <#list listData as article>
<#--         <#if (article_index % 3 == 1)>-->
<#--                            <script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-1229303764778930"-->
<#--                                 crossorigin="anonymous"></script>-->
<#--                            <ins class="adsbygoogle"-->
<#--                                 style="display:block"-->
<#--                                 data-ad-format="fluid"-->
<#--                                 data-ad-layout-key="-f1-g-35-k2+1lw"-->
<#--                                 data-ad-client="ca-pub-1229303764778930"-->
<#--                                 data-ad-slot="5068792338"></ins>-->
<#--                            <script>-->
<#--                                 (adsbygoogle = window.adsbygoogle || []).push({});-->
<#--                            </script>-->
<#--                            </#if>-->
        <#assign articleIds = articleIds + article.oId>
        <#if article_has_next><#assign articleIds = articleIds + ","></#if>
        <#include "common/list-item.ftl">
        </#list>
    </ul>
</div>
</#macro>
<#macro listScript>
<#if articleIds??>
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
<script>
    // Init [Article List] channel
    ArticleListChannel.init("${wsScheme}://${serverHost}:${serverPort}${contextPath}/article-list-channel?articleIds=${articleIds}");
</script>
</#if>
</#macro>
