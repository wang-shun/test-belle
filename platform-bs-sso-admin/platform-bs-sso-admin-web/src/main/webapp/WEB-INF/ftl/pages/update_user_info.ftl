<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>个人资料</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
<#include  "/WEB-INF/ftl/common/header.ftl" >
<script type="text/javascript">
	var loginName='${(Session["session_user"])!}';
	var sureName='${(Session["loginUser"].sureName)!}';
	var email='${(Session["loginUser"].email)!}';
	var phone='${(Session["loginUser"].phone)!}';
	
	$(function(){
		$("#btn").click(
		function(){
			var param = {};
			param['sureName']=$('#sureName').val();
			if($('#sureName').val()==''){
				alert('请输入真实姓名');
				return;
			}
			param['email']=$('#email').val();
			param['phone']=$('#phone').val();
		    $.post(
		    	'${BasePath}/update',
		    	param,
		    	function(result){
		      		alert(result.msg);
		    	}
		    );
		}
		);
		
		$("#loginName").html(loginName);
		$("#sureName").val(sureName);
		$("#email").val(email);
		$("#phone").val(phone);
	});
	
	
	
	console.log(loginName);
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'center',border:false">
 	<form id="fm" method="post" novalidate>
			<div class="fitem">
			<input id="id"  name="id" type="hidden"/>
			</div>	
			
			<div class="fitem">
				<label>登录名:</label><label id="loginName"></label>
			</div>	
			<div class="fitem">
				<label>真实姓名:</label>
				<input id="sureName" class="easyui-validatebox ipt" name="sureName" style="width:200px;" required="true" disabled="disabled"/>
			</div>
			<div class="fitem">
				<label>邮箱:</label>
				<input id="email" class="easyui-validatebox ipt" validType="email" name="email" style="width:200px;"/>
			</div>
			<div class="fitem">
				<label>手机号码:</label>
				<input id="phone" class="easyui-validatebox ipt" validType="phoneNum" name="phone" style="width:200px;"/>
			</div>
			<div class="fitem">
    			<input class="easyui-linkbutton" type="button" id="btn" value="提交" />
    		</div>
		</form>
</div>

</body>
</html>