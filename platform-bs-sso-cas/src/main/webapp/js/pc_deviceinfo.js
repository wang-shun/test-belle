var flag = 0;// 全局变量
var getSystemInfoStep = 0; // 异步按顺序获取系统信息值
var getSystemInfoReturn = 0; // 异步按顺序获取系统信息值

// 获取硬件信息入口，请用setTimeout("getMyHardInfo()", 1000); 的方式调用
function getMyHardInfo() {
	if (flag == 0) {
		getSystemInfoStep = 0;
		getSystemInfoReturn = 0;
		flag = 1;
		// 判断WinSocket是否准备好，如果没准备好，1000毫秒后再取一次
		LODOP = getLodop();
		if ((typeof (LODOP) == "undefined")
				|| (typeof (LODOP.VERSION) == "undefined")
				|| CLODOP.CVERSION < "2.1.2.9") {
			// delALlCookie();
			return "";
		}
		if (($('#BaseBoardValue').val() == '' || $('#DiskDriveValue').val() == '')) {
			if (LODOP.webskt && LODOP.webskt.readyState == 1) {
				setTimeout("getSystemInfo('BIOS.SerialNumber')", 0);
			} else {
				setTimeout("getSystemInfo('BIOS.SerialNumber')", 1000);
			}
		}

	}

}
function getSystemInfo() {
	// alert(getSystemInfoStep);
	// getSystemInfoReturn3秒没获取完结束
	if (getSystemInfoStep <= 6 && getSystemInfoReturn < 17) {
		if (LODOP.webskt && LODOP.webskt.readyState == 1 && !CLODOP.blOneByone) {// blOneByone==true:有窗口已打开
			switch (getSystemInfoStep) {
			case 0:
				getSystemInfoStep = 1;
				getBIOS('BIOS.SerialNumber');
				break;
			case 2:
				getSystemInfoStep = 3;
				getDiskSerialNumber('DiskDrive.1.SerialNumber');
				break;
			case 4:
				getSystemInfoStep = 5;
				getPCName('OperatingSystem.CSName');
				break;
			case 6:
				getSystemInfoStep = 7;
				getOperatingSystem('OperatingSystem.caption');
				break;
			default:
			}

		}
		// alert(getSystemInfoStep);
		setTimeout("getSystemInfo()", 300);
		getSystemInfoReturn++;
	}

}

// 获取BIOS序列号
function getBIOS(strINFOType) {
	if (LODOP.CVERSION)
		CLODOP.On_Return = function(TaskID, value) {
			if (!('NoResult' == value || null == value || 'null' == value || '' == value)) {
				document.getElementById('BaseBoardValue').value = value; // 写入隐藏域
				setCookie("BaseBoardValue", value); // 写H5本次存储或Cookie
			}
			getSystemInfoStep = 2;
			return;
		};
	var strResult = LODOP.GET_SYSTEM_INFO(strINFOType);
	if (!LODOP.CVERSION)
		return strResult;
	else
		return "";
}

// 获取硬盘序列号
function getDiskSerialNumber(strINFOType) {
	if (LODOP.CVERSION)
		CLODOP.On_Return = function(TaskID, value) {
			if (!('NoResult' == value || null == value || 'null' == value || '' == value)) {
				document.getElementById('DiskDriveValue').value = value;
				setCookie("DiskDriveValue", value);
			}
			getSystemInfoStep = 4;
			return;
		};
	var strResult = LODOP.GET_SYSTEM_INFO(strINFOType);
	if (!LODOP.CVERSION)
		return strResult;
	else
		return "";
}

// 获取计算机名称
function getPCName(strINFOType) {
	if (LODOP.CVERSION)
		CLODOP.On_Return = function(TaskID, value) {
			if (!('NoResult' == value || null == value || 'null' == value || '' == value)) {
				document.getElementById('CSNameValue').value = value;
				setCookie("CSNameValue", value);
			}
			getSystemInfoStep = 6;
			return;
		};
	var strResult = LODOP.GET_SYSTEM_INFO(strINFOType);
	if (!LODOP.CVERSION)
		return strResult;
	else
		return "";
}

// 获取操作系统
function getOperatingSystem(strINFOType) {
	if (LODOP.CVERSION)
		CLODOP.On_Return = function(TaskID, value) {
			if (!('NoResult' == value || null == value || 'null' == value || '' == value)) {
				document.getElementById('captionValue').value = value;
				setCookie("captionValue", value);
			}
			getSystemInfoStep = 8;
			return;
		};
	var strResult = LODOP.GET_SYSTEM_INFO(strINFOType);
	if (!LODOP.CVERSION)
		return strResult;
	else
		return "";
}

function setCookie(name, value) {
	if (window.localStorage) { // 游览器支持localStorage
		var storage = window.localStorage;
		storage.setItem(name, value);
	} else {
		if (window.navigator.cookieEnabled) { // 浏览器支持Cookie
			var Days = 30;
			var exp = new Date();
			exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
			document.cookie = name + "=" + escape(value) + ";expires="
					+ exp.toGMTString();
		}
	}
}

function getCookie(name) {
	if (window.localStorage) { // 游览器支持localStorage
		var storage = window.localStorage;
		return storage.getItem(name);
	} else {
		if (window.navigator.cookieEnabled) { // 浏览器支持Cookie
			var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			if (arr = document.cookie.match(reg))
				return unescape(arr[2]);
			else
				return null;
		}
	}
}
function delCookie(name) {
	if (window.localStorage) { // 游览器支持localStorage
		var storage = window.localStorage;
		storage.removeItem(name);
	}
	if (window.navigator.cookieEnabled) { // 浏览器支持Cookie
		var exp = new Date();
		exp.setTime(exp.getTime() - 1);
		var cval = getCookie(name);
		if (cval != null)
			document.cookie = name + "=" + cval + ";expires="
					+ exp.toGMTString();
	}
}
function delALlCookie() {
	delCookie("BaseBoardValue");
	delCookie("DiskDriveValue");
	delCookie("CSNameValue");
	delCookie("captionValue");
}
