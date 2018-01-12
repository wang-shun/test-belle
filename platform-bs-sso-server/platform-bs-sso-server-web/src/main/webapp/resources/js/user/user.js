$(document).keyup(function(event) {
	if (event.keyCode == 13) {
		changePwd();
	}
});

var global = {};

function queryLoginUrl() {
	$.ajax({
		type : "GET",
		url : basePath + "/ssoUser/queryLoginUrl?time=" + new Date().getTime(),
		dataType : "json",
		success : function(data) {
			global = data;
			bizJoinStatus(data);
		},
		error : function(data) {
			alert("查询业务系统地址失败");
		}
	});
};

// 判断是否绑定账号，绑定账号则显示高亮
function bizJoinStatus(data) {

	if (data.OES != "" && data.OES != undefined) {
		$("#oes_target").removeClass("n-inline no-hover");
	}

	if (data.logistics != "" && data.logistics != undefined) {
		$("#logistics_target").removeClass("n-inline no-hover");
	}
};

// 查询绑定信息Map{bizCode:bizName}
function queryBindMap(bizCode) {

	$.ajax({
				type : "GET",
				url : basePath + "/ssoUser/bindStatus?time=" + new Date().getTime(),
				async : false,
				dataType : "json",
				success : function(data) {
					var biz_data = data;
					if (bizCode == "OES") {
						if (biz_data.OES == "" || biz_data.OES == undefined) {
							confirmBindStatus(biz_data, "OES");
							return false;
						} else {
							var oesUrl = global.OES + "?time=" + new Date().getTime()
							window.open(oesUrl);
						}

					}
					if (bizCode == "logistics") {
						if (biz_data.logistics == ""
								|| biz_data.logistics == undefined) {
							confirmBindStatus(biz_data, "logistics");
							return false;
						} else {
							var logisticsUrl = global.logistics + "?time=" + new Date().getTime()
							window.open(logisticsUrl);
						}
					}

				},
				error : function(data) {
					alert(data.msg);
				}
			});
};

// 判断是否绑定账号，绑定账号则显示高亮
function confirmBindStatus(biz_data, bizCode) {

	if (bizCode == "OES") {
		if (biz_data.OES != "" && biz_data.OES != undefined) {
			$("#oes_target").removeClass("n-inline no-hover");
		} else {
			showBindDlgConfirm("OES");
		}
	}

	if (bizCode == "logistics") {
		if (biz_data.logistics != "" && biz_data.logistics != undefined) {
			$("#logistics_target").removeClass("n-inline no-hover");
		} else {
			showBindDlgConfirm("logistics");
		}
	}
};

function bizBindStatus(bindType) {

	if(bindType == "unBindBiz"){
		$("i[name='bingStatus']").removeClass("icon iconfont icon-SSO-binding");
	}	
	
	$.ajax({
		type : "GET",
		url : basePath + "/ssoUser/query_bindinfo?time=" + new Date().getTime(),
		dataType : "json",
		success : function(data) {
			
			for (var i = 0; i < data.length; i++) {
				if ("HR" == data[i].bizCode  && data[i].bizUserName != "") {
					$("#hr_status").attr("class", "icon iconfont icon-SSO-binding");
				}

				if ("OA" == data[i].bizCode && data[i].bizUserName != "") {
					$("#oa_status").attr("class", "icon iconfont icon-SSO-binding");
				}

				if ("logistics" == data[i].bizCode  && data[i].bizUserName != "") {
					$("#logistics_status").attr("class", "icon iconfont icon-SSO-binding");
				}

				if ("OES" == data[i].bizCode && data[i].bizUserName != "") {
					$("#oes_status").attr("class", "icon iconfont icon-SSO-binding");
				}
			}
		},
		error : function(data) {
			alert("更新绑定状态失败");
		}
	});
};

function logout() {
	location.href = basePath + "/logout?time=" + new Date().getTime();
};

function modifyPwd_Rest() {
	$("#modify_userName").val(userName_CAS);
	$("#modify_oldPwd").val("");
	$("#modify_newPwd").val("");
	$("#modify_newPwdAgain").val("");
}

function changePwd() {

	var userName = userName_CAS;
	var oldPassword = $("#modify_oldPwd").val();
	var newPassword = $("#modify_newPwd").val();
	var newPasswordAgain = $("#modify_newPwdAgain").val();
	var regex = /(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,16}$/;

	if (userName == "" || oldPassword == "" || newPassword == ""
			|| newPasswordAgain == "") {
		alert('用户名或密码不能为空！');
		return false;
	}

	if (!regex.test(newPassword)) {
		alert('新密码格式不对，必须6-16位，必须包含字母和数字');
		$("#modify_newPwd").val("");
		$("#modify_newPwdAgain").val("");
		$("#modify_newPwd").focus();
		return false;
	}

	if (oldPassword == newPassword) {
		alert('新密码和原密码不能一致，请重新输入！');
		$("#modify_newPwd").val("");
		$("#modify_newPwdAgain").val("");
		$("#modify_newPwd").focus();
	}

	if (newPassword != newPasswordAgain) {
		alert('新密码两次输入不一致！');
		$("#modify_newPwd").val("");
		$("#modify_newPwdAgain").val("");
		$("#modify_newPwd").focus();
		return false;
	}

	var data = {};
	data.userName = loginName_UID;
	data.oldPassword = oldPassword;
	data.newPassword = newPassword;

	$.ajax({
		type : "POST",
		url : basePath + "/ssoUser/changepwd?time=" + new Date().getTime(),
		data : data,
		success : function(d) {
			if (d.code == 1) {
				showAlert();
			} else {
				alert(d.msg);
			}
		},
		error : function(d) {
			alert(d.msg);
		}
	});
};

// 加载绑定用户信息
function loadBinduserInfo(bizCode) {
	$("#bizUserName").val("");
	$("#bizPassword").val("")

	// 加载业务系统
	getBizConfigInfo(bizCode);

	// 加载绑定信息
	initTable();

	// 显示绑定页面
	$("#bindBizDlg").modal('show');
};

// 查询业务系统列表list
function getBizConfigInfo(defaultBizCode) {
	// 每次查询biz系统bizCode都需要清楚option数据，避免$('#bizNameSelect').append多次累加
	$("#bizNameSelect").find("option").remove();

	$.ajax({
		type : "GET",
		url : basePath + "/ssoUser/queryBizInfo?time=" + new Date().getTime(),
		dataType : "json",
		success : function(data) {
			var result = data
			$('#bizNameSelect').append(
					"<option selected = 'selected'>请选择系统</option>");
			for (var i = 0; i < data.length; i++) {
				$('#bizNameSelect').append(
						"<option id=" + data[i].bizCode + " + value="
								+ data[i].bizCode + ">" + data[i].bizName
								+ "</option>");
				if (defaultBizCode == data[i].bizCode) {
					$("#bizNameSelect").val(defaultBizCode);
				}
			}

		},

		error : function(data) {
			alert("查询失败");
		}
	});
};

// 绑定用户信息-表格数据初始化
function initTable() {
	// 先销毁表格
	$("#bindTable").bootstrapTable('destroy');

	var table = $("#bindTable").bootstrapTable(
			{
				url : basePath + "/ssoUser/query_bindinfo?time=" + new Date().getTime(), // 请求url
				method : 'GET', // 请求方式
				dataType : "json", // 数据类型
				singleSelect : false, // 单选checkbox
				showRefresh : false, // 显示刷新按钮
				showColumns : false, // 选择显示的列
				striped : true, // 是否显示行间隔色
				cache : false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				sortOrder : "asc", // 排序方式
				pagination : true, // 显示分页
				sidePagination : "client", // 数据量很少，直接使用client全文检索分页
				pageNumber : 1, // 初始化加载第一页，默认第一页
				pageSize : 5, // 每页的记录行数（*）
				pageList : [ 10, 25, 50, 100 ], // 可供选择的每页的行数（*）

				columns : [ {
					field : 'state',
					checkbox : true,
					align : 'center'
				}, {
					field : 'userName',
					title : '一账通账号',
					align : 'center'
				}, {
					field : 'sureName',
					title : '真实姓名',
					align : 'center'
				}, {
					field : 'bizCode',
					title : '业务代码',
					visible : 'false'
				}, {
					field : 'bizName',
					title : '业务系统',
					align : 'center'
				}, {
					field : 'bizUserName',
					title : '业务账号',
					align : 'center'
				} ]

			})

	$("#bindTable").bootstrapTable("hideColumn", "bizCode");
};

function bindBizUser() {
	var userName = loginName_UID;
	var bizCode = $("#bizNameSelect").val();
	var bizName = $("#bizNameSelect").find("option:selected").text();
	var bizUserName = $("#bizUserName").val();
	var bizPassword = $("#bizPassword").val();

	if (bizUserName == "") {
		alert("请输入用户名，用户名不能为空");
		return;
	}

	if (bizPassword == "") {
		alert("请输入密码，密码不能为空");
		return;
	}

	var data = {};
	data.bizCode = bizCode;
	data.bizUserName = bizUserName;
	data.bizPassword = bizPassword;

	$.ajax({
		type : "POST",
		url : basePath + "/ssoUser/bind_bizuser?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(data) {
			if (data.code == 1) {
				var sureName = data.sureName;
				var index = 0;
				$("#bindTable").bootstrapTable('insertRow', {
					index : index,
					row : {
						userName : userName,
						sureName : sureName,
						bizCode : bizCode,
						bizName : bizName,
						bizUserName : bizUserName
					}
				});

				bizBindStatus("bindBiz");
			} else {
				alert(data.msg);
			}
			$("#bindDlg").modal('show')
		},
		error : function(data) {
			alert(data.msg);
		}
	});

}

function unBindBizUser() {
	var selectItem = $("#bindTable").bootstrapTable('getSelections');
	var unBindvalues = new Array();
	var bizCodesArray = new Array();
	var bizCodes;
	for (var i = 0; i < selectItem.length; i++) {
		bizCodesArray[i] = selectItem[i].bizCode;
		unBindvalues[i] = selectItem[i].bizName;
	}

	if (selectItem.length == 0) {
		alert("请选择一行删除!");
		return;
	}

	var data = {};
	data.bizCodes = bizCodesArray.join(",");

	$.ajax({
		type : "POST",
		url : basePath + "/ssoUser/unbind_bizuser?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(data) {
			if (data.code == 1) {
				$("#bindTable").bootstrapTable('remove', {
					field : 'bizName',
					values : unBindvalues
				});

				bizBindStatus("unBindBiz");
			} else {
				alert(data.msg);
			}

			$("#bindDlg").modal('show')

		},
		error : function(data) {
			alert("解绑失败!");
		}
	});
}

// 跳转js

function redirectHR() {
	if (global.HR == "" || global.HR == undefined) {
		alert("请先绑定账号！");
		loadBinduserInfo();
		return;
	}
	var hrUrl = global.demo
	window.open(hrUrl);
};

function redirectOES() {
	if (global.OES == "" || global.OES == undefined) {
		alert("请确认配置子系统跳转URL！");
		return;
	}

	queryBindMap("OES");
};

function redirectRetail() {
	if (global.retail == "" || global.retail == undefined) {
		alert("请先绑定账号！");
		loadBinduserInfo();
		return;
	}
	queryBindMap();
};

function redirectOA() {
	if (global.OA == "" || global.OA == undefined) {
		alert("请先绑定账号！");
		loadBinduserInfo();
		return;
	}
	queryBindMap();
};

function redirectLogistics() {
	if (global.logistics == "" || global.logistics == undefined) {
		alert("请确认配置子系统跳转URL！");
		return;
	}
	queryBindMap("logistics");
};

function showUbindConfirm() {
	PostbirdAlertBox.confirm({
		'title' : '提示',
		'content' : '请确认，是否解绑业务系统',
		'okBtn' : '确定',
		'cancelBtn' : '取消',
		'onConfirm' : function() {
			unBindBizUser();
		},
		onCancel : function(data) {
			return false;
		},
	});
};

// TODO
function showBindDlgConfirm(bizCode) {
	PostbirdAlertBox.confirm({
		'title' : '提示',
		'content' : '对不起，请先绑定账户',
		'okBtn' : '确定',
		'cancelBtn' : '取消',
		'onConfirm' : function() {
			loadBinduserInfo(bizCode);
		},
		onCancel : function(data) {
			return false;
		},
	});
};

function showAlert() {
	PostbirdAlertBox.alert({
		'title' : '提示',
		'content' : '用户密码修改成功，请重新登录',
		'okBtn' : '确定',
		'onConfirm' : function() {
			window.location.href = basePath + "/ignore/logout?time=" + new Date().getTime();
		}
	});
};

// 获取sso账户用户资料信息
function getSSOUserInfo() {

	var userName = userName_CAS;
	var data = {};
	data.userName = userName;

	$.ajax({
				type : "GET",
				url : basePath + "/ssoUser/query_userinfo?time=" + new Date().getTime(),
				data : data,
				dataType : "json",
				success : function(data) {
					var result = data
					$("#userName").val(userName);
					$("#sureName").val(result.sureName);
					$("#idCard").val(result.idCard);
					$("#employeeNumber").val(result.employeeNumber);
					$("#email").val(result.email);
					$("#mobile").val(result.mobile);

					if (result.sex == 0) {
						$('#female').prop("checked", true);
					} else {
						$('#male').prop("checked", true);
					}
				},

				error : function(data) {
					alert("查询失败");
				}
			});
};

// 修改sso账户资料
function changeUserInfo() {

	var userName = userName_UID;
	var sureName = $("#sureName").val();
	var sex = $("input:radio[name='sex']:checked").val();
	var idCard = $("#idCard").val();
	var employeeNumber = $("#employeeNumber").val();
	var email = $("#email").val();
	var mobile = $("#mobile").val();

	if (sureName == "" || mobile == "") {
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
		url : basePath + "/ssoUser/update_userinfo?time=" + new Date().getTime(),
		dataType : "json",
		data : data,
		success : function(data) {
			var result = data
			$("userName").val(userName);
			$("sureName").val(result.sureName);
			if (result.sex == 0) {
				$('#female').prop("checked", true);
			} else {
				$('#male').prop("checked", true);
			}
			$("idCard").val(result.idCard);
			$("employeeNumber").val(result.employeeNumber);
			$("email").val(result.email);
			$("mobile").val(result.mobile);
		},

		error : function(data) {
			alert("查询失败");
		}
	});
};