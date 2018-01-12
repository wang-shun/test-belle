function parsePage(){
	/*buildSubSys_scheduler();*/
	
	InitLeftMenu('#leftMenu');

	$('#tabToolsFullScreen').click(function(){
		changeScreen();
	});
	
	addTab({
		title: '系统桌面',
		icon: 'icon-system'
	});
	
}
/**
*根据JSON数据生成子系统
*/
function buildSubSys_scheduler(){
	var callback = function(result){
		buildSubSysCommon(result);
	};
	$.getJSON(BasePath+"/uc_sub_system.json",callback);
}

//初始化左侧菜单
function InitLeftMenu(obj) {
	 /*$.getJSON("sso_user/get_menu_data", function(data){
		
	 });*/
	var menu = '[{'+
	        '"parentId": 1,'+
	        '"menuLevel": 2,'+
	        '"state": "open",'+
	        '"systemId": 111,'+
	        '"systemName": null,'+
	        '"childrenCount": 0,'+
	        '"menuCode": null,'+
	        '"attributes": {},'+
	        '"id": 111,'+
	        '"text": "统一登录平台",'+
	        '"remark": null,'+
	        '"order": 1,'+
	        '"isLeaf": "false",'+
	        '"children": ['+
	            '{'+
	                '"parentId": 111,'+
	                '"menuLevel": null,'+
	                '"state": "open",'+
	                '"systemId": 111,'+
	                '"systemName": "统一登录平台",'+
	                '"childrenCount": 0,'+
	                '"menuCode": "1",'+
	                '"attributes": {},'+
	                '"id": 10001,'+
	                '"text": "系统管理",'+
	                '"remark": "",'+
	                '"order": 1,'+
	                '"isLeaf": "true",'+
	                '"children": [';
	var ssoUser = '{'+
    '"parentId": null,'+
    '"menuLevel": null,'+
    '"state": "open",'+
    '"systemId": null,'+
    '"systemName": null,'+
    '"childrenCount": 0,'+
    '"menuCode": null,'+
    '"attributes": {'+
    '   "url": "sso_user/userPage"'+
    '},'+
    '"id": 100001,'+
    '"text": "一账通用户管理",'+
    '"remark": null,'+
    '"order": null,'+
    '"isLeaf": null,'+
    '"children": null'+
    '}';
	var bizConfig = '{'+
   '"parentId": null,'+
   '"menuLevel": null,'+
   '"state": "open",'+
   '"systemId": null,'+
   '"systemName": null,'+
   '"childrenCount": 0,'+
   '"menuCode": null,'+
   '"attributes": {'+
       '"url": "biz_config/index"'+
   '},'+
   '"id": 100002,'+
   '"text": "系统配置管理",'+
   '"remark": null,'+
   '"order": null,'+
   '"isLeaf": null,'+
   '"children": null'+
   '}';
	var admin = '{'+
   '"parentId": null,'+
   '"menuLevel": null,'+
   '"state": "open",'+
   '"systemId": null,'+
   '"systemName": null,'+
   '"childrenCount": 0,'+
   '"menuCode": null,'+
   '"attributes": {'+
       '"url": "sso_admin/index"'+
   '},'+
   '"id": 100003,'+
   '"text": "管理员管理",'+
   '"remark": null,'+
   '"order": null,'+
   '"isLeaf": null,'+
   '"children": null'+
	'}';
	var role = '{'+
   '"parentId": null,'+
   '"menuLevel": null,'+
   '"state": "open",'+
   '"systemId": null,'+
   '"systemName": null,'+
   '"childrenCount": 0,'+
   '"menuCode": null,'+
   '"attributes": {'+
       '"url": "admin_role/index"'+
   '},'+
   '"id": 100003,'+
   '"text": "角色管理",'+
   '"remark": null,'+
   '"order": null,'+
   '"isLeaf": null,'+
   '"children": null'+
	'}';
	var loginUserInfo = '{'+
   '"parentId": null,'+
   '"menuLevel": null,'+
   '"state": "open",'+
   '"systemId": null,'+
   '"systemName": null,'+
   '"childrenCount": 0,'+
   '"menuCode": null,'+
   '"attributes": {'+
       '"url": "updateUserInfo"'+
   '},'+
   '"id": 100003,'+
   '"text": "个人信息管理",'+
   '"remark": null,'+
   '"order": null,'+
   '"isLeaf": null,'+
   '"children": null'+
	'}';
	
	var changePassword = '{'+
	   '"parentId": null,'+
	   '"menuLevel": null,'+
	   '"state": "open",'+
	   '"systemId": null,'+
	   '"systemName": null,'+
	   '"childrenCount": 0,'+
	   '"menuCode": null,'+
	   '"attributes": {'+
	       '"url": "updateUserPwd"'+
	   '},'+
	   '"id": 100003,'+
	   '"text": "修改密码",'+
	   '"remark": null,'+
	   '"order": null,'+
	   '"isLeaf": null,'+
	   '"children": null'+
		'}';
	
	var testPrint = '{'+
	   '"parentId": null,'+
	   '"menuLevel": null,'+
	   '"state": "open",'+
	   '"systemId": null,'+
	   '"systemName": null,'+
	   '"childrenCount": 0,'+
	   '"menuCode": null,'+
	   '"attributes": {'+
	       '"url": "testPrint"'+
	   '},'+
	   '"id": 100003,'+
	   '"text": "测试打印",'+
	   '"remark": null,'+
	   '"order": null,'+
	   '"isLeaf": null,'+
	   '"children": null'+
		'}';
	
	if(adminType==1){ //系统管理员
		menu += ssoUser;
		menu += ",";
		menu += bizConfig;
		menu += ",";
		menu += admin;
		menu += ",";
		menu += role;
		menu += ",";
		menu += loginUserInfo;
		menu += ",";
		menu += changePassword;
		menu += ",";
		menu += testPrint;
	}else if(adminType==2){ //超级管理员
		menu += ssoUser;
		menu += ",";
		menu += admin;
		menu += ",";
		menu += role;
		menu += ",";
		menu += loginUserInfo;
		menu += ",";
		menu += changePassword;
		menu += ",";
		menu += testPrint;
	}else { //普通管理员
		menu += ssoUser;
		menu += ",";
		menu += loginUserInfo;
		menu += ",";
		menu += changePassword;
		menu += ",";
		menu += testPrint;
	}
	                
	menu +=       ']'+
	            '}'+
	        ']'+
	    '}'+
	']';
	var data = JSON.parse(menu);
	buildMenuCommon(data[0].children);
}
