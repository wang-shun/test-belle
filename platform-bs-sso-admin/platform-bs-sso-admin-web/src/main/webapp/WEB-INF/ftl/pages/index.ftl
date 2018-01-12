<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="Keywords" content="一账通后台管理系统" />
<meta name="Description" content="一账通后台管理系统" />
<title>一账通后台管理系统</title>

<#include  "/WEB-INF/ftl/common/header.ftl" >

</head>
<body class="easyui-layout" data-options="fit:true,border:false">

<div class="header" data-options="
	region: 'north',
	border: false
">
	<div class="wrapper">
        <div style=" float: left;position: absolute;top: 5px;z-index: 2;width: auto;height: 56px;line-height: 56px;padding-left: 50px;">
            <h1 style="font-size:35px">一账通后台管理系统</h1>
        </div>
        <div class="nav">
            <div class="system">
                <span class="welcome">你好，<#if Session.adminType==1>系统管理员<#elseif Session.adminType==2>超级管理员<#else>普通管理员</#if>${Session.session_user!""}！欢迎进入！</span>
                <span class="logout">
					<a href="<@s.url '/logout'/>">退出</a>
				</span>
           	</div>            
			<div id="subSystem" class="subSys"></div>
        </div>
	</div>
</div>

<div style="width: 180px;" title="左侧菜单" data-options="
    region:'west',
	split:true,
	minSplit:true
">
    <div id="leftMenu"></div>
</div>

<div data-options="
    region:'center'
">
    <div id="mainTabs" class="easyui-tabs" data-options="fit:true,border:false">
        <div title="系统桌面"  data-options="icon:'icon-home'">
            <div class="pd10">
                <img src="${domainStatic}/resources/images/welcome.jpg"/>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript"> var adminType = ${Session.adminType};</script>
<script type="text/javascript" src="${domainStatic}/resources/common/js/index.js?version=0.05" ></script>
</body>
</html>
