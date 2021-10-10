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
<#-- TODO: RM -->
<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${hotLabel} - ${symphonyLabel}">
        <meta name="description" content="${recentArticleLabel}"/>
        </@head>
        <link rel="canonical" href="${servePath}/hot">
                        <link rel="stylesheet" href="${staticServePath}/css/main.css">
        <link rel="stylesheet" href="${staticServePath}/css/sortable.min.css">
        <script type="text/javascript" src="${staticServePath}/js/sortable.min.js"></script>
                        <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />

    </head>
    <body>
        <#include "header.ftl">
        <div class="main">
               <main class="sortable">
               <div class="container2">
                 <div class="wrapper2">
                   <ul class="sortable__nav nav">
                     <li> <a data-sjslink="all" class="nav__link"> 全部 </a> </li>
                     <li> <a data-sjslink="wx" class="nav__link"> 公众号 </a> </li>
                     <li> <a data-sjslink="blog" class="nav__link"> 博客 </a> </li>
                   </ul>
                   <div id="sortable" class="sjs-default">
                     <div data-sjsel="wx">
                       <div class="card"> <img class="card__picture" src="https://img.6aiq.com/2021/10/qrcode-a2867c5c.bmp" alt="">
                         <div class="card-infos">
                           <h2 class="card__title">DataFunTalk</h2>
                           <p class="card__text"> 专注于大数据、人工智能技术应用的分享与交流。致力于成就百万数据科学家。定期组织技术分享直播，并整理大数据、推荐/搜索算法、广告算法、NLP 自然语言处理算法、智能风控、自动驾驶、机器学习/深度学习等技术应用文章。 </p>
                         </div>
                       </div>
                     </div>
                     <div data-sjsel="blog">
                        <div class="card">
                        <a href="https://kirivir.github.io/?from=www.6aiq.com" target="_blank"><img class="card__picture" src="https://kirivir.github.io/images/logo/logo@144.png" alt=""></a>
                          <div class="card-infos">
                            <h2 class="card__title">KIRI 的个人博客</h2>
                            <p class="card__text">有趣的算法分享，理性的工作感悟
                                                  每周一篇！ </p>
                          </div>
                        </div>
                      </div>
                 </div>
               </div>
               </div>
               <script type="text/javascript">
               	    document.querySelector('#sortable').sortablejs()
               	  </script>
        </div>
        <#include "common/domains.ftl">
        <#include "footer.ftl">
    </body>
</html>
