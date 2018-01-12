// 前置修改密码请求

function forceChangePwd() {
	var userName = $("#fcUserName").val();
	$("fcUserName").val(userName);

	var fcPassword = $("#fcPassword").val();
	var fcPasswordAgain = $("#fcPasswordAgain").val();
	var fcOldPassword = $("#fcOldPassword").val();
	var regex = /(?!^[0-9]+$)(?!^[A-z]+$)(?!^[^A-z0-9]+$)^.{6,16}$/;

	if (fcOldPassword == "") {
		alert("原始密码不能为空，请输入原始密码");
		return;
	}
	if (fcPassword == "" || fcPasswordAgain == "") {
		alert("新密码不能为空，请输入新密码");
		return;
	}
	if (!regex.test(fcPassword)) {
		alert('新密码6-16位，必须包含字母和数字，请重新输入！');
		$("#fcPassword").val("");
		$("#fcPasswordAgain").val("");
		$("#fcPassword").focus();
		return;
	}

	if (fcPassword == fcOldPassword) {
		alert('新密码和原始密码不能一致，请重新输入！');
		$("#fcPassword").val("");
		$("#fcPasswordAgain").val("");
		$("#fcPassword").focus();
		return;
	}

	if (fcPassword != fcPasswordAgain) {
		alert('新密码两次输入不一致，请重新输入！');
		$("#fcPassword").val("");
		$("#fcPasswordAgain").val("");
		$("#fcPassword").focus();
		return;
	}

	var data = {};
	data.userName = session_UID;
	data.newPassword = fcPassword;
	data.oldPassword = fcOldPassword;

	$.ajax({
		type: "POST",
		url : basePath + "/ignore/orgpwd_rest?time=" + new Date().getTime(),
		dataType: "json",
		data: data,
		success: function (result) {
			if (result.code == 1) {
				showAlert();
			} else {
				alert(result.msg);
				$("#fcPassword").val("");
				$("#fcPasswordAgain").val("");
				$("#fcOldPassword").val("");
				$("#fcOldPassword").focus();
			}
		},
		error: function (result) {
			alert(result.msg);
			$("#fcPassword").val("");
			$("#fcPasswordAgain").val("");
			$("#fcOldPassword").val("");
			$("#fcOldPassword").focus();
		}
	});
};

function showAlert() {
	PostbirdAlertBox.alert({
		'title' : '提示',
		'content' : '强制修改密码成功，请重新登录',
		'okBtn' : '确定',
		'onConfirm' : function() {
			console.log("回调触发后隐藏提示框");
			window.location.href = basePath + "/ignore/logout?time="
					+ new Date().getTime();
		}
	});
}