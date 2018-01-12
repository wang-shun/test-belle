<%--
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
--%>
<jsp:directive.include file="includes/top.jsp" />
<%@ page pageEncoding="UTF-8"%>

<!-- 登录 -->
<div class="login-main">
	<div class="login-box">
		<div class="modal-body login" id="login">
			<h4 class="modal-title user-select-none ">一账通账号</h4>

			<!-- 登陆 -->
			<form:form class="aBillFrom clearfix" method="post" id="fm1"
				commandName="${commandName}" htmlEscape="true">
				<form:errors path="*" id="msg" cssClass="errors" element="div"
					htmlEscape="false" />
				<div class="form-group user-form">
					<!--  <input type="text" class="form-control" placeholder="用户名" autocomplete="off">  -->
					<form:input class="form-control" id="username" placeholder="用户名"
						size="25" tabindex="1" accesskey="${userNameAccessKey}"
						path="username" autocomplete="off" htmlEscape="true" />
				</div>
				<div class="form-group">
					<!-- <input type="password" class="form-control" placeholder="密码" autocomplete="off">  -->
					<form:password class="form-control" id="password" placeholder="密码"
						size="25" tabindex="2" path="password"
						accesskey="${passwordAccessKey}" htmlEscape="true"
						autocomplete="off" />
				</div>
				<div class="form-group loginCodeFrom  clearfix">
					<div class="codeBox float-right">
						<!--  <input type="text" class="form-control float-left codeInput" maxlength="4" id="code" placeholder="验证码" autocomplete="off">  -->
						<form:input class="form-control float-left codeInput" id="captcha"
							placeholder="验证码" size="4" tabindex="3" path="captcha"
							accesskey="${captchaAccessKey}" htmlEscape="true"
							autocomplete="off" maxlength="4" />
						<div class="code float-right loginCode">
							<img style="cursor: pointer; vertical-align: middle;"
								src="captchaCode"
								onClick="this.src='captchaCode?time='+new Date().getTime();" />

							<!-- <img style="cursor: pointer; vertical-align: middle;"
								src="captcha.jsp"
								onClick="this.src='captcha.jsp?time'+Math.random();"/> -->
						</div>
					</div>
				</div>
				<div class="form-bar">
					<span class="form-bar-item divice-btn" id="look_deviceInfo">查看设备</span>
					<span class="form-bar-item forget-psd-btn" id="forgetPwd">忘记密码?</span>
				</div>
				<!-- 
          <input id="warn" name="warn" value="true" tabindex="4" accesskey="<spring:message code="screen.welcome.label.warn.accesskey" />" type="checkbox" />
           -->

				<!-- ******************设备信息******************* -->
				<!-- 主板BIOS序列号信息 -->
				<input type="hidden" name="BaseBoardValue" id="BaseBoardValue">
				<!-- 硬盘序列号信息 -->
				<input type="hidden" name="DiskDriveValue" id="DiskDriveValue">
				<!-- 计算机名称 -->
				<input type="hidden" name="CSNameValue" id="CSNameValue">
				<!-- 计算机操作系统 -->
				<input type="hidden" name="captionValue" id="captionValue">

				<input type="hidden" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />

				<input class="btn btn-primary btn-from" name="submit" type="submit"
					accesskey="l"
					value="<spring:message code="screen.welcome.button.login" />"
					tabindex="5" />
				<!-- 
				<span class="old-enter" id="orgLogin"> 旧版入口 <i
					class="icon iconfont icon-xiangyoujiantou"></i>
				</span>
				 -->
			</form:form>
			<!-- 查看设备基础信息 -->
			<div class="deviceInfo">
				<i class="icon iconfont icon-guanbi close-icon"></i>
				<h3 class="deviceTitle">登记设备状态</h3>
				<div>
					<label>设备编号：</label> <span id="deviceCode">null_null</span>
				</div>
				<div>
					<label>设备名称：</label> <span id="deviceName">null</span>
				</div>
				<div>
					<label>设备类型：</label> <span id="deviceType">null</span>
				</div>
				<div>
					<label>操作系统：</label> <span id="operatorSys">null</span>
				</div>
				<div>
					<label>设备状态：</label> <span id="deviceStatus">功能已屏蔽</span>
				</div>
				<div>
					<label>设备备注：</label> <span id="deviceNotice" class="text-danger">该功能暂时不开启，请与管理员联系</span>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	//全局变量：忘记密码url
	var forgetPwdUrl = "";

	// 全局变量：旧版入口url
	var orgLoginUrl = "";

	// 查看设备信息

	$("#look_deviceInfo").click(function() {
		// 获取设备信息
		getMyHardInfo();

		// 显示设备信息
		deviceInfo_Render();

	});

	function deviceInfo_Render() {

		if (getCookie("BaseBoardValue") != null
				&& getCookie("DiskDriveValue") != null
				&& getCookie("CSNameValue") != null
				&& getCookie("captionValue") != null) {
			$('#BaseBoardValue').val(getCookie("BaseBoardValue"));
			$('#DiskDriveValue').val(getCookie("DiskDriveValue"));
			$('#CSNameValue').val(getCookie("CSNameValue"));
			$('#captionValue').val(getCookie("captionValue"));

			if (($('#BaseBoardValue').val() == '' || $('#DiskDriveValue').val() == '')) {
				setTimeout("getDeviceInfo()", 0);
			} else {
				setTimeout("getDeviceInfo()", 200);
			}
			return;
		}
		setTimeout("getMyHardInfo()", 200);
	}

	function getDeviceInfo() {
		var BaseBoardValue = $("#BaseBoardValue").val();
		if (BaseBoardValue == "" || BaseBoardValue == null
				|| BaseBoardValue == undefined) {
			BaseBoardValue = getCookie("BaseBoardValue");
		}

		var DiskDriveValue = $("#DiskDriveValue").val();
		if (DiskDriveValue == "" || DiskDriveValue == null
				|| DiskDriveValue == undefined) {
			DiskDriveValue = getCookie("DiskDriveValue");
		}

		var CSNameValue = $("#CSNameValue").val();
		if (CSNameValue == "" || CSNameValue == null
				|| CSNameValue == undefined) {
			CSNameValue = getCookie("CSNameValue");
		}

		var captionValue = $("#captionValue").val();
		if (captionValue == "" || captionValue == null
				|| captionValue == undefined) {
			captionValue = getCookie("captionValue");
		}

		var devCode = BaseBoardValue + "_" + DiskDriveValue;

		if (BaseBoardValue == '' || BaseBoardValue == null
				|| BaseBoardValue == 'null' || devCode == 'null_null') {
			printer.doLoadInfo();
			if (printer.printerMessageshow()) {
				return;
			}
		}

		$("#deviceCode").text(devCode);
		$("#deviceName").text(CSNameValue);
		$("#deviceType").text("PC设备")
		$("#operatorSys").text(captionValue);
	}

	function getCasLinkUrl() {

		$.ajax({
			type : "GET",
			url : "${pageContext.request.contextPath}/link?time="
					+ new Date().getTime(),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data.code == 1) {
					forgetPwdUrl = data.forgetPwdUrl;
					orgLoginUrl = data.orgLoginUrl;
				} else {
					alert(data.msg);
				}
			},
			error : function(data) {
				alert("查询失败!" + data);
			}
		});
	};

	$("#forgetPwd").click(
			function() {
				if (forgetPwdUrl == "" || forgetPwdUrl == null
						|| forgetPwdUrl == undefined) {
					getCasLinkUrl();
				}
				window.location.href = forgetPwdUrl;
			});

	$("#orgLogin").click(
			function() {
				if (orgLoginUrl == "" || orgLoginUrl == null
						|| orgLoginUrl == undefined) {
					getCasLinkUrl();
				}
				window.open(orgLoginUrl);
			});
</script>

<jsp:directive.include file="includes/bottom.jsp" />
