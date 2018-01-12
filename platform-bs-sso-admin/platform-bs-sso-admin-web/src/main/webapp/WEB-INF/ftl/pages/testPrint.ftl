    <!DOCTYPE html>
    <html>
    <head>
    	<meta charset="UTF-8">
    	<#include  "/WEB-INF/ftl/common/header.ftl" >
    	<script type="text/javascript" src="${domainStatic}/resources/js/testPrint.js"></script>
    	<script type="text/javascript" src="${domainStatic}/resources/common/LodopFuncs.js"></script>
		<link href="${domainStatic}/resources/css/styles/login/base.css?${staticVersion!}" rel="stylesheet" />
	    <link href="${domainStatic}/resources/css/styles/login/login.css?${staticVersion!}" rel="stylesheet" />
    </head>
    <body>
    	<a href="javascript:void(0);" onclick="print(0)" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px">打印</a>
		
    </body>
    
    </html>