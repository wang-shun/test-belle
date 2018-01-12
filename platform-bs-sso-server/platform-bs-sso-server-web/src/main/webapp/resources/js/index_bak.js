$(document).ready(function () {
  bindUi()
  remind()
})

// 控制是否登录
function controlUser() {
//  $(".noLogin").toggle();
//  $(".hasLogin").toggle();
}

function bindUi() {
  // 注册按钮
//  $("#reginBtn").on('click', function () {
//    controlTab()
//  })

  // 登录按钮
//  $("#loginBtn").on('click', function () {
//    controlUser()
//    $('#myModal').modal('hide')
//  })

  // 我要注册按钮
//  $("#goRegin").on('click', function () {
//    controlTab()
//  })

  // 重置密码按钮
//  $("#clearBtn").on('click', function () {
//    $("#password").val("")
//  })

  // 打开修改密码弹出框
  $("#modifypsw").on('click', function () {
    $("#pswDlg").modal('show')
  })
  
  // 打开绑定用户弹出框
  $("#binduser").on('click', function () {
    $("#bindDlg").modal('show')
  })
  
  //打开修改用户资料弹出框
 $("#modifyuser").on('click', function () {
    $("#userDlg").modal('show')
  })
  
   //打开修改用户资料弹出框
 $("#select").on('click', function () {
    $("#selectDlg").modal('show')
  })
  
  
  

$("#modifyPswBtn").on('click', function () {
   // controlTab()
  }) 
  
}


 

// 控制弹出框界面切换
function controlTab() {
//  $(".tab-content").toggle();
//  $(".modal-lg-tile").toggle();
//  $(".regin").toggle();
//  $(".modal-rg-tile").toggle()
  //$("#pswDlg").toggle()
  
}
// 提示功能
function remind() {
  var remindWord = "@wonhigh.cn";
  var $username = $("#username");
  var $userRemind = $(".user-remind");
  var $remindItem = $(".remind-item");

  $username.on('input', function () {
    var thisVal = $(this).val()
    if (thisVal.length > 0) {
      $userRemind.show()
      if (thisVal.indexOf("@wonhigh.cn") === -1) {
        $remindItem.text($("#username").val() + "@wonhigh.cn")
      }
    } else {
      $userRemind.hide()
    }
  })
  $remindItem.on('click', function () {
    $username.val($(this).text());
    $(this).text("");
    $userRemind.hide()
  })
}