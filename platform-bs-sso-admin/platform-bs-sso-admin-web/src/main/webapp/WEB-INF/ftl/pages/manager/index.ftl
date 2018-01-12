<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#include  "/WEB-INF/ftl/common/header.ftl" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>SSO-Admin</title>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.min.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.easyui.min.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.menu.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.menubutton.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.searchbox.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.pagination.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.datagrid.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.edatagrid.js?version=${staticVersion}"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/locale/easyui-lang-zh_CN.js?version=${staticVersion}"></script>
   <link rel="stylesheet" href="${domainStatic}/resources/jquery-easyui-1.5.3/themes/default/easyui.css?version=${staticVersion}" type="text/css"/>
   <link rel="stylesheet" href="${domainStatic}/resources/jquery-easyui-1.5.3/themes/icon.css?version=${staticVersion}" type="text/css"/>
   
   <script type="text/javascript">

		$(function(){
		
			$('#ss').searchbox({
		        searcher:function(value,name){
		        	 var p = {};
		        	 p[name]=value;
		        	 var state = $('#state').val();
		        	 if(state.length!=0){
		        	 	p['state']=state;
		        	 }
		        	 var emp=$('#employeeType').val();
		        	 if(emp.length!=0){
		        	 	p['employeeType']=emp;
		        	 }
		        	 var roleName = $('#roleName').val();
		        	 if(roleName.length!=0){
		        	 	p['roleName']=roleName;
		        	 }
	        	     $('#tt').edatagrid('load', p);
		        },
		        menu:'#mm',
		        prompt:'Please Input Value'
		    });
		
			var qParams = {};
			$('#tt').edatagrid({
				url: '${BasePath}/ssoManager/list',
				saveUrl: '${BasePath}/ssoManager/add',
				updateUrl: '${BasePath}/ssoManager/update',
				destroyUrl: '${BasePath}/ssoManager/del',
				autoSave: false,
				rownumbers: true,
				pagination: true,
				toolbar: '#tb',
				striped: true,
				nowrap: false,
				idField:'loginName',
				singleSelect:false,
				pageSize:5,
		        pageList:[2,5,20,30],
		        
				queryParams:qParams,
				
				onClickRow: function (rowIndex, rowData) { //单击不显示黄色背景
	                $(this).datagrid('unselectRow', rowIndex);
	   			},		
	   			
				onDestroy: function(index,row){
					$(this).datagrid('reload');
				},
	   			
				onError: function(index,row){
					alert(row.msg);
				},
				
				onEdit: function(index,row){
					var lineClass = '#datagrid-row-r1-2-'+index;
					 $(lineClass +" >td").each(function(){
					 	if($(this).attr("field")=="loginName"){
					 		$(this).find("input").attr("readonly","readonly");
					 	}
                    }) 
				},
				
				onSave: function(index,row){
					alert(row.msg);
				},
				
			    destroyMsg:{
			    	norecord:{	// when no record is selected
			    		title:'Warning',
			    		msg:'没有选中任何记录.'
			    	},
			    	confirm:{	// when select a row
			    		title:'Confirm',
			    		msg:'确定删除?'
			    	}
			    },
	   			
				columns:[[
					{field:'selectFlag',title:'选中', width:20,checkbox:true, align:'center'
					},
					{field:'loginName',title:'用户名', width:150,align:'center', required:'true',fixed:'true',
						editor:{  
						    type:'validatebox',  
						    options:{  
						        required:true
						    }  
					    }
					},
					{field:'sureName',title:'真实姓名', width:80,align:'center',fixed:'true',editor:'text',
						editor:{  
						    type:'validatebox',  
						    options:{  
						        required:true
						    }  
					    }
					},
					{field:'role',title:'管理员角色', width:90,align:'center',
						editor:{  
						    type:'combobox',  
						    options: {  
	                            data: ${roleList},  
	                            valueField: "v",  
	                            textField: "t",  
	                            editable: false,  
	                            panelHeight:70,  
	                            required: true  
	                        }  
					    }
					},
					{field:'state',title:'状态', width:50,align:'center',
						formatter: function(value,row,index){
							if(value==1){
								return "正常";
							} else if(value==2){
								return "已锁定";
							}
						},
						editor:{  
						    type:'combobox',  
						    options: {  
	                            data: [{"v":"1","t":"正常"},{"v":"2","t":"已锁定"}],
	                            valueField: "v",  
	                            textField: "t",  
	                            editable: false,  
	                            panelHeight:70,  
	                            required: true  
	                        }  
					    }
					},
					{field:'updateTime',title:'更新时间', width:150,align:'center'
					}
				]]
	   			
			});
			
		    var pager = $('#tt').datagrid('getPager');
			pager.pagination({
				pageSize:5,
		        pageList:[2,5,10,15,20],
		        pageNumber:1,
				showPageList:true
			});
			
		    
			
		});
		
		function updatePwd(){
			var row = $('#tt').datagrid('getSelected');
			if (row){
				var loginName = row.loginName;
				var param = {};
				param['loginName'] = loginName;
				$.post(
		    	'${BasePath}/ssoManager/updatePwd',
		    	param,
		    	function(result){
		      		alert(result.msg);
				    if(result.code==1){
				    }
		    	}
		    );
			}else{
				alert("请选择一个用户");
			}
		};
		
	</script>
</head>

    
<body >
	<div class="easyui-layout" data-options="fit:true">
	
		<div region="north" title="一账通后台管理系统" style="padding:5px;">
		</div>
		
    	<div region="west" split="true" title="菜单" style="width:300px;">
		    <div class="easyui-accordion" style="width:100%;overflow: hidden" data-options="fit:false,border:false,multiple:false">
		    	
		    	<div title="SSO用户管理" style="width:100%;overflow:hidden;padding:10px" >
		    		<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc" >
						<a href="${BasePath}/ssoUser/index" class="easyui-linkbutton" plain="true" >用户列表</a>
					</div>
		    	</div>
		    	<#if Session["adminRole"]?exists > 
		    	<#assign adminRole = Session["adminRole"]>
		    	<#if adminRole=="superAdmin">
		    	<div title="管理员管理" style="width:100%;overflow:hidden;padding:10px" selected="true">
		    		<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc">
						<a href="${BasePath}/ssoManager/index" class="easyui-linkbutton" plain="true" >管理员列表</a>
					</div>
		    	</div>
		    	<div title="管理员角色管理" style="width:100%;overflow:hidden;padding:10px" >
		    		<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc">
						<a href="${BasePath}/adminRole/index" class="easyui-linkbutton" plain="true" >管理员角色</a>
					</div>
		    	</div>
		    	<div title="业务系统配置管理" style="width:100%;overflow:hidden;padding:10px"  >
		    		<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc">
						<a href="${BasePath}/bizConfig/index" class="easyui-linkbutton" plain="true" >业务系统列表</a>
					</div>
		    	</div>
		    	</#if>
		    	</#if>
		    	<div title="个人信息管理" style="width:100%;overflow:hidden;padding:10px" >
		    		<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc" >
						<a href="${BasePath}/updateUserInfo" class="easyui-linkbutton" plain="true" >个人资料</a>
					</div>
					<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc" >
						<a href="${BasePath}/updateUserPwd" class="easyui-linkbutton" plain="true" >修改密码</a>
					</div>
					<div style="padding:5px;background:#fafafa;width:100%;border:1px solid #ccc; ">
						<a href="${BasePath}/logout" class="easyui-linkbutton" plain="true"  >退出登录</a>
					</div>
		    	</div>
		    </div>
		</div>
    	
    	<div id="content" region="center" title="" style="padding:5px;width:100%;height:100%">
    	
		    <div style="margin-bottom:10px" id="tb">
		    
		    	<span>职位:</span>
				<select id="roleName" class="easyui-combobox" name="roleName" style="width:150px;">
					<option value="">全部</option>
					<#if roleList2??>
						<#list roleList2 as role>
							<option value="${role.roleName}">${role.roleName}</option>
						</#list>
					</#if>
				</select>
				<span>状态:</span>
				<select id="state" class="easyui-combobox" name="state" style="width:100px;">
					<option value="">全部</option>
				    <option value="1">正常</option>
				    <option value="2">已锁定</option>
				</select>
				<input id="ss"></input>
			    <div id="mm" style="width:200px">
			    	<div data-options="name:'all'">选择搜索条件</div>
			        <div data-options="name:'loginName'">用户名</div>
			        <div data-options="name:'sureName'">真实姓名</div>
			    </div>
			
			    <span class="easyui-linkbutton" plain="true" ></span>
				<a href="#" class="easyui-linkbutton" onclick="javascript:$('#tt').edatagrid('addRow')">新增</a>
				<a href="#" class="easyui-linkbutton" onclick="javascript:$('#tt').edatagrid('saveRow')">保存</a>
				<a href="#" class="easyui-linkbutton" onclick="javascript:$('#tt').edatagrid('cancelRow')">取消编辑</a>
				<a href="#" class="easyui-linkbutton" onclick="javascript:$('#tt').edatagrid('destroyRow')">删除</a>
				<a href="#" class="easyui-linkbutton" onclick="updatePwd()">重置密码</a>
			</div>
	    	<table id="tt" style="width:100%;height:100%"
				title="sso用户列表"
				singleSelect="true">
				<thead>
					<tr>
						<th field="selectFlag" checkbox="true" ></th>
						<th field="loginName" width="100" align="center" editor="{type:'validatebox',options:{required:true}}">用户名</th>
						<th field="sureName" width="100" align="center" editor="text">真实姓名</th>
						<th field="employeeType" width="100" align="center" editor="text">管理员级别</th>
						<th field="state" width="100" align="center" editor="{type:'validatebox',options:{required:true}}">状态</th>
						<th field="updateTime" width="100" align="center" editor="datebox" >更新时间</th>
					</tr>
				</thead>
			</table>
    	
		    
    	</div>
    	
    </div>

</body>


</html>
