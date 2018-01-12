<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>百丽统一登录平台</title>
  <#include  "/WEB-INF/ftl/common/header.ftl" >
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/login.css?version=${staticVersion}" />
  <script type="text/javascript" src="${domainStatic}/resources/js/user/pwd_force.js?version=${staticVersion}" ></script>
    
</head>

<body>

  <!-- 登录 -->
  <main class="login-main">
    <div class="login-box">
     
      <!-- 强制修改密码 -->
      <div class="modal-body loginPsw">
        <h4 class="modal-title hoverDefault ">首次登录修改密码</h4>
        <form class="aBillFrom clearfix">
          <div class="form-group user-form">
          	<!-- readonly= "true" -->
            <input type="text" id="fcUserName" class="form-control" placeholder="用户名" autocomplete="off" value="${(Session["editPwdUserName"])!}" readonly= "true">
          </div>
          <div class="form-group">
            <input type="password" id="fcOldPassword" class="form-control " placeholder="请输入初始密码" autocomplete="off">
          </div>
          <div class="form-group">
            <input type="password" id="fcPassword" class="form-control " placeholder="新密码6-16位，必须包含字母和数字" autocomplete="off">
          </div>
          <div class="form-group  form-group-last">
            <input type="password" id="fcPasswordAgain" class="form-control " placeholder="确认新密码6-16位，必须包含字母和数字" autocomplete="off">
          </div>
         	<input type="button" class="btn btn-primary btn-from" id="btn_save" value="保存" onclick="forceChangePwd()">
        </form>
      </div>

  </main>
</body>

</html>