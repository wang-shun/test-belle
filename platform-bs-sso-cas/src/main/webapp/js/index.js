$(document).ready(function() {
  bindUi();
  deviceInfo_Render();
});

function bindUi() {
	
	// 测试打开弹出框
	$(".openServive").on("click", function() {
		$("#myModal").modal("show");
	});

	$(".update-pwd-btn").on("click", function() {
		$("#updatePwd").modal("show");
	});

	//绑定业务系统
	$(".bind-bizsys-btn").on("click", function() {
		$("#bindBizDlg").modal("show");
	});

	// 修改密码
	$(".phoneCode").on("click", function() {
		$(".tips-list").toggle();
	});

	$(".tips-list-item").on("click", function() {
		$(".tips-list").hide();
		$(".phone-input").attr("placeholder", $(this).text());
	});
	
	$(".deviceInfo .close-icon").on("click", function() {
	    $(".deviceInfo").toggle();
	  });
	
	$(".divice-btn").on("click", function() {
	    $(".deviceInfo").toggle();
		  });
}

// 提示功能
//function remind() {
//  var remindWord = "@wonhigh.cn";
//  var $username = $("#username");
//  var $userRemind = $(".user-remind");
//  var $remindItem = $(".remind-item");
//
//  $username.on("input", function() {
//    var thisVal = $(this).val();
//    if (thisVal.length > 0) {
//      $userRemind.show();
//      if (thisVal.indexOf("@wonhigh.cn") === -1) {
//        $remindItem.text($("#username").val() + "@wonhigh.cn");
//      }
//    } else {
//      $userRemind.hide();
//    }
//  });
//  $remindItem.on("click", function() {
//    $username.val($(this).text());
//    $(this).text("");
//    $userRemind.hide();
//  });
//}

	
