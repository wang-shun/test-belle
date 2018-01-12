   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.easyui.min.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/locale/easyui-lang-zh_CN.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/common/js/jquery.form.js?version=${staticVersion}" ></script>
   <script type="text/javascript" src="${domainStatic}/resources/js/date.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/common/js/jquery-1.6.4.min.js?version=${staticVersion}"></script>
<script type="text/javascript" src="${domainStatic}/resources/common/other-lib/common.js?version=${staticVersion}" ></script>

<script type="text/javascript" src="${commonDomainStatic}/common/2.2.4/boot.js?version=${staticVersion}"></script>

<link rel="stylesheet" type="text/css" href="${domainStatic}/resources/css/styles/sys-window.css?version=${staticVersion}" />

<script>
var BasePath = '${springMacroRequestContext.getContextPath()}';
</script>
<!--界面上直接用   -->
<#assign BasePath = springMacroRequestContext.getContextPath()/>

<script>
	   var BasePath = '${springMacroRequestContext.getContextPath()}';
	   
	   $.extend($.fn.validatebox.defaults.rules, {   
		    phoneNum: { //验证手机号  
		        validator: function(value, param){
			        var pattern = /^1[6345789]\d{9}$/; 
					return pattern.test(value); 
		        },   
		        message: '请输入正确的手机号码。'  
		    },
		    
		    email: { //验证邮箱
		        validator: function(value, param){
		        	// /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
			        var pattern = /^[A-Za-z]+[0-9]*([-_.][A-Za-zd]+)*@([A-Za-zd]+[-.])+[A-Za-zd]{2,5}$/; 
					return pattern.test(value); 
		        },   
		        message: '请输入正确的邮箱。'  
		    },
		   
		    adminPwd:{ //密码必须由6-12位数字加字母组成
		      	validator: function(value, param){
		          var pattern = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$/; 
				  return pattern.test(value);
		        },   
		        message: '密码必须由6-12位数字加字母组成。'
		    },
		   
		    telNum:{ //既验证手机号，又验证座机号
		      validator: function(value, param){
		          return /(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\d3)|(\d{3}\-))?(1[358]\d{9})$)/.test(value);
		         },   
		         message: '请输入正确的电话号码。'
		    },
		   
		    loginNameLength:{ //校验用户名长度
		      validator: function(value, param){
		          var l = value.length;
		          if(l>32){
		          	return false;
		          }
		          return true;
		         },   
		         message: '用户名长度不能超过32位。'
		    },
		    
		    idCardLength:{
		    	validator: function(value, param){
		    		return /^[0-9A-Za-z]{1,20}$/.test(value);
		         },   
		         message: '身份证号不超过20位，只能包含数字和字母。'
		    },
		    
		    //时间比较
    		afterDate : {
			      validator : function(value,param) {
			       //jquery对象
			       var _this = this;
			       var res = true;
			       if($(param[0]).length >= 1){
			        $(param[0]).each(function(){
			         var pdate = Date.parse2($(this).val());
			         if(Date.parse2(value) <= pdate){
			          res = false;
			          param[0] = pdate.format('yyyy-MM-dd hh:mm:ss');
			          //$.data(_this,"validatebox").options.rules[afterDate]
			          //.message="该日期要比"+param[0]+"大";
			         }
			        });
			        
			       }
			       return res;
			      },
			    message : "该日期要{0}大"
			},
		     //时间比较
		     beforeDate : {
			      validator : function(value,param) {
			       //jquery对象
			       var _this = this;
			       var res = true;
			       if($(param[0]).length >= 1){
			        $(param[0]).each(function(){
			         var pdate = Date.parse2($(this).val());
			         if(Date.parse2(value) > pdate){
			          res = false;
			          param[0] = pdate.format('yyyy-MM-dd hh:mm:ss');
			          //$.data(_this,"validatebox").options.rules[afterDate]
			          //.message="该日期要比"+param[0]+"大";
			         }
			        });
			        
			       }
			       return res;
			      },
			    message : "该日期要{0}小"
		     },
			 afterCurrentDate : {
			      validator : function(value,param) {
			       var cud = new Date();
			       cud.setHours(0, 0, 0, 0);
			       return Date.parse2(value)>cud;
			      },
			   message : "该日期要比当前时间"+(new Date).format('yyyy-MM-dd')+"大"
			}
		});
</script>
