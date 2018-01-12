  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/font-awesome-4.7.0/css/font-awesome.min.css?version=${staticVersion}" />
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/iconFont/iconfont.css?version=${staticVersion}" />
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/bootstrap/bootstrap.min.css?version=${staticVersion}" /> 
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/bootstrap/bootstrap-table.css?version=${staticVersion}" /> 
  <link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/postbirdAlertBox/postbirdAlertBox.min.css?version=${staticVersion}" /> 
   
  <script type="text/javascript" src="${domainStatic}/resources/js/jquery.min.js?version=${staticVersion}"></script>
  <script type="text/javascript" src="${domainStatic}/resources/js/bootstrap/popper.min.js?version=${staticVersion}" ></script>
  <script type="text/javascript" src="${domainStatic}/resources/js/bootstrap/bootstrap.min.js?version=${staticVersion}" ></script>
  <script type="text/javascript" src="${domainStatic}/resources/js/bootstrap/bootstrap-table.js?version=${staticVersion}" ></script>
  <script type="text/javascript" src="${domainStatic}/resources/js/bootstrap/i18n/bootstrap-table-zh-CN.js?version=${staticVersion}" ></script>
  <script type="text/javascript" src="${domainStatic}/resources/js/postbirdAlertBox/postbirdAlertBox.min.js?version=${staticVersion}" ></script>

	
<!--界面上直接用   ${basePath!}  -->
<#assign basePath = springMacroRequestContext.getContextPath()/>

<script>
	var basePath = '${springMacroRequestContext.getContextPath()}';
	
	var sureName_CHS = '${(Session["sureName_CHS"])!}';
	
	var loginName_UID = '${(Session["loginName_UID"])!}';
	
	var userName_CAS = '${(Session["userName_CAS"])!}';
	
	var session_UID = '${(Session["uid"])!}';
</script>
	
	
	

