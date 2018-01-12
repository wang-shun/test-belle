<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>百丽统一登录平台</title>
  
 <!--界面上直接用   {basePath}  -->
  <#assign basePath = springMacroRequestContext.getContextPath()/>
  
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/index.css" />   
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/font-awesome-4.7.0/css/font-awesome.min.css" />
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/iconFont/iconfont.css" />
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/bootstrap/bootstrap.min.css" /> 
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/bootstrap/bootstrap-select.css" /> 
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/bootstrap/bootstrap-table.css" /> 
  <link rel="stylesheet" type="text/css" href="${basePath}/resources/css/styles/postbirdAlertBox/postbirdAlertBox.min.css" /> 
   
  <script type="text/javascript" src="${basePath}/resources/js/jquery.min.js"></script>
  <script type="text/javascript" src="${basePath}/resources/js/index.js"></script> 
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/popper.min.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/bootstrap.min.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/bootstrap-select.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/i18n/bootstrap-select-zh_CN.min.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/bootstrap-table.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/bootstrap/i18n/bootstrap-table-zh-CN.js" ></script>
  <script type="text/javascript" src="${basePath}/resources/js/postbirdAlertBox/postbirdAlertBox.min.js" ></script>
  
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
          <i class="icon iconfont icon-user"></i>你好！${(Session["loginName"])!}</li>
        <li id="btn_modifyPwd" class="nav-list-item update-pwd-btn">
          <i class="icon iconfont icon-lock"></i>修改密码</li>
        <li id="btn_binduser" class="nav-list-item bind-bizsys-btn">
          <i class="icon iconfont icon-link"></i>绑定业务系统</li>
        <li id="btn_logout" class="nav-list-item" onclick="window.location.href='${basePath}/webhome/logout'">
          <i class="icon iconfont icon-out"></i>退出</li>
      </ul>
    </nav>
  </header>

  <!-- 主体 -->
  <main>
    <ul class="menu-list">
      <li id="hr_target" class="menu-item" >
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-hr"></i>E-HR
      </li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-shop"></i>零售系统</div>
      </li>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-CRM"></i>棋星CRM</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-BI"></i>数据分析BI</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-car"></i>物流系统</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-OA"></i>OA系统</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-data"></i>集团主数据</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-OCP"></i>DCP云店</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-sport"></i>体育BI</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-project"></i>工程系统</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-money"></i>财务系统</li>
      </div>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-bigData"></i>百丽大数据</li>
      </div>
      </li>
      <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-README"></i>README</li>
      </div>
      </li>
       <li class="menu-item">
        <div class="d-flex">
          <i class="icon iconfont icon-SSO-weiketang"></i>微课堂</li>
      </div>
      </li>
      <li class="menu-item"></li>

      <li class="menu-item"></li>
      <li class="menu-item"></li>
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
              <input type="password" id="modify_newPwd" class="form-control " placeholder="新密码 6-16位，数字字母字符至少2种" autocomplete="off">
            </div>
            <div class="form-group  form-group-last">
              <input type="password" id="modify_newPwdAgain" class="form-control " placeholder="确认新密码 6-16位，数字字母字符至少2种" autocomplete="off">
            </div>
             <input type="button" class="btn btn-primary btn-from" id="changePWD" value="保存">
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
					<select id="bizNameSelect" class="selectpicker" data-selected-text-format="count > 3" style="width: 80px">
			  		</select>
                </div>
                <div class="form-group">
                  <input id="bizUserName" type="text" class="form-control" autofocus placeholder="业务系统账号" autocomplete="off">

                </div>
                <div class="form-group">
                  <input id="bizPassword" type="password" class="form-control " autofocus placeholder="业务系统密码" autocomplete="off">
                </div>
              </div>

              <input type="button" id="btn_bind" class="btn btn-primary float-right"  onclick="bindBizUser()" value="绑定"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true">
              <input type="button" id="btn_unbind" class="btn btn-primary float-left"  onclick="unBindBizUser()"value="解绑"><span class="glyphicon glyphicon-remove" aria-hidden="true">
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
  
   <!-- 修改密码的错误信息 -->
  <script type="text/javascript">
    $(document).keyup(function(event){
        if(event.keyCode ==13){
            changePwd();
        }
    });
    
    var global = {};
    
    $(document).ready(function(){
    	$.ajax({
			type : "GET",
            url: "${basePath}/ssoUser/queryLoginUrl",
            dataType: "json",
            success: function (data) {
            	global = data;
            },
            error: function (data) {
                alert("查询失败" + data);
            }
        });
    });
    
    
    $("#btn_modifyPwd").on("click",function(){
    	$("#modify_userName").val("${(Session["loginName"])!}");
    	$("#modify_oldPwd").val("");
    	$("#modify_newPwd").val("");
    	$("#modify_newPwdAgain").val("");
    });
    
    $("#changePWD").on("click", function() {
    	
    	var userName = '${(Session["loginName"])!}';
        var oldPassword = $("#modify_oldPwd").val();
        var newPassword = $("#modify_newPwd").val();
        var newPasswordAgain = $("#modify_newPwdAgain").val();
        var regex = /(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,16}$/;
        
        if (userName=="" || oldPassword=="" ||newPassword=="" || newPasswordAgain=="") {
            alert('用户名或密码不能为空！');
            return false;
        }
        
        if(!regex.test(newPassword)){
        	alert('新密码格式不对，需要6-16位，数字字母字符至少2种！');
        	$("#modify_newPwd").val("");
    		$("#modify_newPwdAgain").val("");
    		$("#modify_newPwd").focus();
            return false;
        }
        
        if(newPassword != newPasswordAgain)
        {
        	alert('新密码两次输入不一致！');
        	$("#modify_newPwd").val("");
    		$("#modify_newPwdAgain").val("");
    		$("#modify_newPwd").focus();
            return false;
        }
        
        
        var data = {};
        data.userName = userName;
        data.oldPassword = oldPassword;
        data.newPassword = newPassword;
        
        $.ajax({
            type : "POST",
            url : "${basePath}/ssoUser/changepwd",
            data : data,
            success : function(d) {
                if (d.code == 0) {
                   alert('用户密码修改失败', d.msg);
                } else {
                   showAlert();
                }
            }
        });
    });
    
  
   $('#bizNameSelect').selectpicker({
            noneSelectedText: "==请选择==",
        });
        
        
  	$("#btn_binduser").bind("click", function(){
  		getBizConfigInfo();
  		initTable();
  	}); 
        
   
   function getBizConfigInfo(){
		$.ajax({
			type : "GET",
            url: "${basePath}/ssoUser/queryBizInfo",
            dataType: "json",
            success: function (data) {
            	var result = data
                for (var i = 0; i < data.length; i++) {
                    $('#bizNameSelect').append("<option id=" + data[i].bizCode +" + value=" + data[i].bizCode + ">" + data[i].bizName + "</option>");
                }
                 $('#bizNameSelect').selectpicker('refresh');
                 $('#bizNameSelect').selectpicker('render');
            },
            
            error: function (data) {
                alert("查询失败" + data);
            }
        });
    };
    
    // 获取sso账户用户资料信息
    function getSSOUserInfo(){
    	
    	var userName = '${(Session["loginName"])!}';
    	var data = {};
    	data.userName = userName;
    
		$.ajax({
			type : "GET",
            url: "${basePath}/ssoUser/query_userinfo",
            data: data,
            dataType: "json",
            success: function (data) {
            	var result = data
            	$("#userName").val(userName);
            	$("#sureName").val(result.sureName);
            	$("#idCard").val(result.idCard);
            	$("#employeeNumber").val(result.employeeNumber);
            	$("#email").val(result.email);  
            	$("#mobile").val(result.mobile);    
            	
            	if(result.sex == 0)
            	{
            		$('#female').prop("checked", true);
            	}else{
            		$('#male').prop("checked", true);
            	}       
            },
            
            error: function (data) {
                alert("查询失败" + data);
            }
        });
    };
    
    // 修改sso账户资料
    function changeUserInfo(){
    	
    	var userName = '${(Session["loginName"])!}';
    	var sureName = $("#sureName").val();
    	var sex = $("input:radio[name='sex']:checked").val();
    	var idCard = $("#idCard").val();
    	var employeeNumber = $("#employeeNumber").val();
    	var email = $("#email").val();
    	var mobile = $("#mobile").val();
    	
    	if(sureName == "" || mobile == "" ){
    		alert('真是姓名或手机号不能为空！');
            return false;
    	}
    	
    	var data = {};
    	data.userName = userName;
    	data.sureName = sureName;
    	data.sex = sex;
    	data.idCard = idCard;
    	data.employeeNumber = employeeNumber;
    	data.email = email;
    	data.mobile = mobile;
    
		$.ajax({
			type : "POST",
            url: "${basePath}/ssoUser/update_userinfo",
            dataType: "json",
            data: data,
            success: function (data) {
            	var result = data
            	$("userName").val(userName);
            	$("sureName").val(result.sureName);
            	if(result.sex == 0)
            	{
            		$('#female').prop("checked", true);
            	}else{
            		$('#male').prop("checked", true);
            	}
            	$("idCard").val(result.idCard);
            	$("employeeNumber").val(result.employeeNumber);
            	$("email").val(result.email);           
            	$("mobile").val(result.mobile);           
            },
            
            error: function (data) {
                alert("查询失败" + data);
            }
        });
    };
    
    function initTable()
    {
    	 //先销毁表格  
         $("#bindTable").bootstrapTable('destroy');  
    	
    	 var table = $("#bindTable").bootstrapTable(
	    		{	
					url: "${basePath}/ssoUser/query_bindinfo",  // 请求url
	    			method: 'GET',			  					// 请求方式
	    			dataType: "json",		  					// 数据类型
	    			singleSelect: false,   					    // 单选checkbox
	    			showRefresh: false,                         // 显示刷新按钮  
        			showColumns: false,                         // 选择显示的列 
        			striped: true,                      		//是否显示行间隔色
           			cache: false,                       		//是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
           			sortOrder: "asc",                   		//排序方式
        			pagination: true,							// 显示分页
        			sidePagination: "client",                   // 数据量很少，直接使用client全文检索分页
        			pageNumber: 1,                      		//初始化加载第一页，默认第一页
            		pageSize: 5,                       			//每页的记录行数（*）
            		pageList: [10, 25, 50, 100],        		//可供选择的每页的行数（*）
	    		
	    			columns: [
						{
							field: 'state',
							checkbox:true,
							align: 'center'
	    				},
	    				{
							field: 'userName',
							title: '一账通账号',
							align: 'center'
	    				},
	    				{
							field: 'sureName',
							title: '真实姓名',
							align: 'center'
	    				},
	    				{
							field: 'bizCode',
							title: '业务代码',
							visible: 'false'
	    				},
	    				{
							field: 'bizName',
							title: '业务系统',
							align: 'center'
	    				},
	    				{
							field: 'bizUserName',
							title: '业务账号',
							align: 'center'
	    				}
	    			]
	    			
	    		})
	    		
	    $("#bindTable").bootstrapTable("hideColumn","bizCode");
    };
    
    $(document).ready(function () {          
        //调用函数，初始化表格  
        //initTable();  
  
        //当点击查询按钮的时候执行  
        //$("#btn_bind").bind("click", initTable);  
        //$("#btn_unbind").bind("click", initTable);
    }); 
    
    
    function bindBizUser(){
    	var userName = '${(Session["loginName"])!}';
    	var bizCode = $("#bizNameSelect").selectpicker("val");
    	var bizName = $("#bizNameSelect").find("option:selected").text();
    	var bizUserName = $("#bizUserName").val();
    	var bizPassword = $("#bizPassword").val();
    	
    	var data = {};
    	data.bizCode = bizCode;
    	data.bizUserName = bizUserName;
    	data.bizPassword = bizPassword;
    	
    	$.ajax({
			type : "POST",
            url: "${basePath}/ssoUser/bind_bizuser",
            dataType: "json",
            data: data,
            success: function (data) {
            	if(data.code == 1)
            	{
            		var sureName = data.sureName;
	    		 	var index = 0;
			    	 $("#bindTable").bootstrapTable('insertRow', {
			            index: index,
			            row: {
			                userName: userName,
			                sureName: sureName,
			                bizCode: bizCode,
			                bizName: bizName,
			                bizUserName: bizUserName
			            }
			        });
            		//alert("绑定成功" + data);
            	}else{
            		alert("绑定失败" + data.msg);
            	}
            	$('#bizNameSelect').selectpicker('refresh');
                $('#bizNameSelect').selectpicker('render');
            	$("#bindDlg").modal('show')
            },
            error: function (data) {
                alert("绑定失败" + data);
            }
        });
    	
    }
    
    function unBindBizUser(){
    	var selectItem = $("#bindTable").bootstrapTable('getSelections');
    	var unBindvalues=new Array();
    	var bizCodesArray = new Array();
    	var bizCodes;
    	for(var i=0;i<selectItem.length;i++)
		{
			bizCodesArray[i] = selectItem[i].bizCode;
			unBindvalues[i] = selectItem[i].bizName;
		}
		
        if (selectItem.length ==0 ) {
            alert("请选择一行删除!");
            return;
        }
    	
    	var data = {};
    	data.bizCodes = bizCodesArray.join(",");
    	
    	$.ajax({
			type : "POST",
            url: "${basePath}/ssoUser/unbind_bizuser",
            dataType: "json",
            data: data,
            success: function (data) {
            	if(data.code == 1)
            	{
            		 $("#bindTable").bootstrapTable('remove', {
			            field: 'bizName',
			            values: unBindvalues
			        });
            		
            		//alert("解绑成功!" + data);
            	}else{
            		alert("解绑失败!" + data.msg);
            	}
            	
            	$('#bizNameSelect').selectpicker('refresh');
                $('#bizNameSelect').selectpicker('render');
            	$("#bindDlg").modal('show')
            	
            },
            error: function (data) {
                alert("解绑失败!" + data);
            }
        });
    }
    
    function queryAllLoginUrl(){
		    	
    }
    
	$("#hr_target").on("click",function target()
	{
		var hrUrl = global.demo
		window.open(hrUrl);
		//window.open('http://localhost:8082/platform-bs-sso-admin-web/login')
	});
	
    function showAlert() {
        PostbirdAlertBox.alert({
            'title': '提示',
            'content': '用户密码修改成功',
            'okBtn': '好的',
            'onConfirm': function () {
                $('#updatePwd').modal('hide');
            }
        });
    }   
</script>
  
  

</body>

</html>