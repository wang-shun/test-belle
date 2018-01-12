<!DOCTYPE html>
<html>
<head>
    <title>SSO统一登录管理平台</title>
    <meta charset="UTF-8">
	<#include  "/WEB-INF/ftl/common/header.ftl" >
	<link href="${domainStatic}/resources/css/styles/login/base.css?version=${staticVersion}" rel="stylesheet" />
    <link href="${domainStatic}/resources/css/styles/login/login.css?version=${staticVersion}" rel="stylesheet" />
</head>

<body class="blf1-body">
    <div class="container">
        <div class="blf1-login">
            <h1 class="logo">统一登录管理后台</h1>
            <span><font size="+2" color="red"  style="font-size:9p">${error!}</font></span>
            <form class="form-horizontal" action="<@s.url "/login" />" method="post" id="dataForm">
             <input type="hidden" name="flag" value="submit" >
              <input type="hidden" name="cookieFlag" id="cookieFlag" value='0'>
                <div class="form-group username">
                    <input type="text" class="form-control" name="loginName"  id="loginName" value="${loginName!}" placeholder="用户名" class="easyui-validatebox" required="true" missingMessage='请输入用户名!'  />
                </div>
                <div class="form-group password">
                    <input type="password" class="form-control" id="loginPassword" name="loginPassword"  placeholder="密码" class="easyui-validatebox" required="true" missingMessage='请输入密码!' />
                </div>
                <div class="form-group">
                    <button type="submit" class="btn-login" value="" onclick="checkForm();">登录</button>
                </div>
            </form>
        </div><!-- blf1 login -->
        <div class="blf1-banner"></div>
        <p class="copyright">Copyright &copy; 2015 云盛海宏信息技术（深圳）有限公司 - 统一登录管理后台</p>
    </div><!-- container -->
</body>

<!--[if lt IE 9]>
<script src="${domainStatic}/resources/common/other-lib/placeholder.js" />" ></script>
<script>
$("#loginName,#loginPassword").placeholder({isUseSpan:true});
</script>
<![endif]--> 
<script>

function checkForm(){

	var fromObj=$('#dataForm');
	 var validateForm= fromObj.form('validate');
	     if(validateForm==false){
	          return ;
	     }
	     
	if($('#loginName').val()==''){
		$('#loginName').focus();
		return false;
	}
	
	if($('#loginPassword').val()==''){
		$('#loginPassword').focus();
		return false;
	}
	

	
	$('#dataForm').submit();
}

  document.onkeydown = function(e){
    
    
        var e = e || window.event;
        var keyCode = e.keyCode || e.which;
        var tg = e.srcElement || e.target;
        if(keyCode == 13){
            checkForm();
        }
	}
	


</script>
</html>