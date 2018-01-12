<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>百丽统一登录平台</title>
  
  <#include  "/WEB-INF/ftl/common/header.ftl" >
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/login.css?version=${staticVersion}" /> 
  <script type="text/javascript" src="${domainStatic}/resources/js/user/pwd_modify.js?version=${staticVersion}" ></script>
</head>

<body>

	<!-- 重置一账通账密码 -->
  <main class="login-main">
    <div class="login-box">
      <div class="modal-body login updatePwd" id="restPWDDlg">
      	<i id="btn_close" class="icon iconfont icon-guanbi close-icon-link close-icon" onclick="closeModifyDlg()"></i>
        <h4 class="modal-title user-select-none ">重置一账通密码</h4>

        <!-- 重置一账通账密码 -->
        <form class="aBillFrom clearfix">
          <div class="form-group user-form">
            <input type="text" id="restUserName" class="form-control  is-invalid" placeholder="用户名" autocomplete="off" onblur="communicationMethod()">
          </div>
          <div class="form-group">
          	<div class="d-flex link-pannel">
          		<div>
			    	<select id="restCheckMethod" onchange="changeCommuMethod()">
						<option selected="selected" value="phone">手机</option>
						<option value="email">邮箱</option>
					</select>
				</div>
				<div>
					<input type="text" class="form-control" id="restSelectValue" placeholder="手机或者邮箱" autocomplete="off" disabled>
				</div>
			</div>
  		 </div>
          <div class="form-group   clearfix">
            <div class="codeBox float-right">
              <input type="text" id="restCheckCode" class="form-control float-left codeInput" placeholder="验证码" autocomplete="off">
              <div>
                  <input type="button" id="sendVerfiCode" class="btn btn-primary float-right" value="获取验证码" onclick="sendVerificationCode()">
              </div>
            </div>
       
          </div>
          <div class="form-group">
            <input type="password" id="restNewPwd" class="form-control" placeholder="新密码6-16位，必须包含字母和数字" autocomplete="off">
          </div>
          <div class="form-group">
            <input type="password" id="restNewPwdAgain" class="form-control" placeholder="确认新密码6-16位，必须包含字母和数字" autocomplete="off">
          </div>
          <input type="button" class="btn btn-primary btn-from" id="reginBtn" onclick="restPwd()" value="保存">
        </form>
      </div>

</body>

</html>