<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>百丽统一登录平台</title>
  
  <#include  "/WEB-INF/ftl/common/header.ftl" >
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/index.css?version=${staticVersion}" />
  <script type="text/javascript" src="${domainStatic}/resources/js/index.js?version=${staticVersion}"></script>   
  <script type="text/javascript" src="${domainStatic}/resources/js/user/user.js?version=${staticVersion}" ></script>
 
</head>

<body>
  <!-- 头部开始 -->
  <header>
    <nav class="navbar">
      <div class="log">
        <h3 class="navbar-brand cnName hoverDefault" href="#">百丽国际统一登录平台</h3>
        <p class="navbar-brand enName hoverDefault" href="#">Belle Unified Login Platform</small>
      </div>
      <ul class="nav-list">
        <li class="nav-list-item user">
          <i class="icon iconfont icon-user"></i>你好！${(Session["sureName_CHS"])!}</li>
        <li id="btn_modifyPwd" class="nav-list-item update-pwd-btn" onclick="modifyPwd_Rest()">
          <i class="icon iconfont icon-lock"></i>修改密码</li>
        <li id="btn_binduser" class="nav-list-item bind-bizsys-btn" onclick="loadBinduserInfo()">
          <i class="icon iconfont icon-link"></i>绑定业务系统</li>
        <li id="btn_logout" class="nav-list-item" onclick="logout()">
          <i class="icon iconfont icon-out"></i>退出</li>
      </ul>	
    </nav>
  </header>

  <!-- 主体 -->
  <main>
  	<ul class="menu-list">
  	
  	  <!--onclick="redirectRetail()"-->
      <li class="menu-item n-inline no-hover" id="retail_target" onclick="redirectRetail()">
        <div class="bingding">
          <i id="retail_status" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-shop"></i>零售系统
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <!-- onclick="redirectLogistics()"-->
      
      <li class="menu-item n-inline no-hover" id="logistics_target" onclick="redirectLogistics()">
      	<div class="bingding">
          <i id="logistics_status" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-car"></i>物流系统
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      
      <!--
      <li class="menu-item n-inline no-hover">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-pingtai"></i>物流平台
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id=""></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-diaodu"></i>物流调度中心
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id=""></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-peisong"></i>城市配送
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      -->
      <!-- onclick="redirectHR()" -->
      <li class="menu-item n-inline no-hover" id="hr_target" >
      	<div class="bingding">
          <i id="hr_status" name="bingStatus"></i>
        </div>
        <div id="hr_status" class="d-flex">
          <i class="icon iconfont icon-SSO-hr"></i>E-HR
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      
      <!-- onclick="redirectOA()" -->
      <li class="menu-item n-inline no-hover" id="oa_target" >
      	<div class="bingding">
          <i id="oa_status" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-OA"></i>OA系统
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-BI"></i>数据分析BI
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-sport"></i>体育BI
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-bigData"></i>百丽数据平台
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item n-inline no-hover" id="oes_target" onclick=" redirectOES()">
      	<div class="bingding">
          <i id="oes_status" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-weiketang"></i>在线考试OES
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-OCP"></i>OCP云店
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
        <div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-money"></i>财务系统
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-CRM"></i>棋星CRM
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-data"></i>集团主数据
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-project"></i>工程系统
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
      <li class="menu-item  n-inline no-hover">
      	<div class="bingding">
          <i id="" name="bingStatus"></i>
        </div>
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-README"></i>REDMINE
        </div>
        <div class="d-online">
          <p class="text-danger">暂未开放，敬请期待</p>
        </div>
      </li>
    </ul>
  </main>


  <!-- 修改密码  -->
  <div class="modal fade changePsd" id="updatePwd">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <i class="icon iconfont icon-guanbi close-icon close-icon-updatepwd" onclick="javascript:$('#updatePwd').modal('hide')"></i>
        <div class="modal-body">
          <h4 class="modal-title hoverDefault ">修改一账通密码</h4>
          <form class="aBillFrom clearfix">
            <div class="form-group user-form">
              <input id="modify_userName" type="text" class="form-control" autofocus placeholder="用户名" autocomplete="off" readonly="true"> 
              <!-- border-danger类控制input框颜色 -->
            </div>
            <div class="form-group">
              <input type="password" id="modify_oldPwd" class="form-control " placeholder="原密码 " autocomplete="off">
            </div>
            <div class="form-group">
              <input type="password" id="modify_newPwd" class="form-control " placeholder="新密码6-16位，必须包含字母和数字" autocomplete="off">
            </div>
            <div class="form-group  form-group-last">
              <input type="password" id="modify_newPwdAgain" class="form-control " placeholder="确认新密码6-16位，必须包含字母和数字" autocomplete="off">
            </div>
             <input type="button" class="btn btn-primary btn-from" id="changePWD" value="保存" onclick="changePwd()">
          </form>
        </div>
      </div>
    </div>
  </div>

  <!-- 绑定账号 -->
  <div class="modal fade linkService" id="bindBizDlg">
    <div class="modal-dialog modal-lg" role="">
      <div class="modal-content">
        <i class="icon iconfont icon-guanbi close-icon-link close-icon" onclick="javascript:$('#bindBizDlg').modal('hide')"></i>
        <div class="modal-body">
          <article class="input-box">
            <h4 class="modal-title hoverDefault ">绑定业务系统</h4>
            <form class="aBillFrom clearfix ">
              <div class="d-flex link-pannel">
                <div class="form-group select-form">
					<select id="bizNameSelect">
			  		</select>
                </div>
                <div class="form-group">
                  <input id="bizUserName" type="text" class="form-control" autofocus placeholder="业务系统账号"  autocomplete="off">

                </div>
                <div class="form-group">
                  <input id="bizPassword" type="text" class="form-control " autofocus placeholder="业务系统密码"  onfocus="this.type='password'" autocomplete="off">
                </div>
              </div>

              <input type="button" id="btn_bind" class="btn btn-primary float-right"  onclick="bindBizUser()" value="绑定"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true">
              <input type="button" id="btn_unbind" class="btn btn-primary float-left"  onclick="showUbindConfirm()"value="解绑"><span class="glyphicon glyphicon-remove" aria-hidden="true">
            </form>
          </article>
          <article class="account-box">
            <h5 class="account-title hoverDefault">已绑定账号</h5>
            <div class="account-list">
            	<table id="bindTable" data-toggle="table" ></table>
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
  

</body>

</html>