    <!DOCTYPE html>
    <html>
    <head>
    	<meta charset="UTF-8">
    	<#include  "/WEB-INF/ftl/common/header.ftl" >
    	<script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.textbox.js"></script>
    	<script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.filebox.js"></script>
		<link href="${domainStatic}/resources/css/styles/login/base.css?${staticVersion!}" rel="stylesheet" />
	    <link href="${domainStatic}/resources/css/styles/login/login.css?${staticVersion!}" rel="stylesheet" />
    </head>
    <body>
    	<a href="javascript:void(0);" onclick="importClick()" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px">导入</a>
    	<div class="easyui-window" id="import-excel-template" title="文件上传" style="width:400px;height:160px;padding:2px;" closed="true">
		    <form id="importFileForm" method="post" enctype="multipart/form-data" style="display:none">
		        <table style="margin:5px;height:70px;">
		            <tr>
		                <td>请选择文件</td>
		                <td width="5px;"></td>
		                <td><input type="file" class="easyui-filebox" id="fileImport" name="fileImport" style="width:260px;"></td>
		                <td></td></tr>
		            <tr>
		                <td colspan="4"><label id="fileName" /></td>
		            </tr>
		            <tr>
		                <td colspan="4">
		                    <label id="uploadInfo" />
		                </td>
		            </tr>
		        </table><div style="text-align:center;clear:both;margin:5px;">
		            <a id="uploadFile" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="importFileClick()" href="javascript:void(0)">上传</a>
		            <a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="closeImportClick()" href="javascript:void(0)">关闭</a>
		        </div>
		    </form>
		</div>
		
    </body>
    <script>
    	var BasePath = '${springMacroRequestContext.getContextPath()}';
    	//导入事件，显示导入弹出窗口
	    this.importClick = function ()
	    {
	        $('#import-excel-template').window('open')
	　　　　 document.getElementById("importFileForm").style.display = "block";
	    }
	    //关闭导入弹出窗口
	    this.closeImportClick = function () {
	        document.getElementById('fileImport').value = null;
	        document.getElementById('fileName').innerHTML = "";
	        document.getElementById('uploadInfo').innerHTML = "";
	        $('#import-excel-template').window('close')
	    }
	    
	    //导入文件
	    this.importFileClick = function ()
	    {
	        //获取上传文件控件内容
	        var file = document.getElementById('fileImport').files[0];
	        //判断控件中是否存在文件内容，如果不存在，弹出提示信息，阻止进一步操作
	        if (file == null) { alert('错误，请选择文件'); return; }
	        //获取文件名称
	        var fileName = file.name;
	        //获取文件类型名称
	        var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
	        //这里限定上传文件文件类型必须为.xlsx，如果文件类型不符，提示错误信息
	        if (file_typename == '.xlsx')
	        {
	            //计算文件大小
	            var fileSize = 0;
	            //如果文件大小大于1024字节X1024字节，则显示文件大小单位为MB，否则为KB
	            if (file.size > 1024 * 1024) {
	
	　　　　　　　　　　fileSize = Math.round(file.size * 100 / (1024 * 1024)) / 100;
	
	　　　　　　　　　if (fileSize > 10) {
	                    alert('错误，文件超过10MB，禁止上传！'); return;
	                }
	　　　　　　　　　fileSize = fileSize.toString() + 'MB';
	            }
	            else {
	                fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
	            }
	            //将文件名和文件大小显示在前端label文本中
	            document.getElementById('fileName').innerHTML = "<span style='color:Blue'>文件名: " + file.name + ',大小: ' + fileSize + "</span>";
	            //获取form数据
	            var formData = new FormData($("#importFileForm")[0]);
	            //调用apicontroller后台action方法，将form数据传递给后台处理。contentType必须设置为false,否则chrome和firefox不兼容
	            $.ajax({
	                url: BasePath+"/sso_user/resetPwd",
	                type: 'POST',
	                data: formData,
	                async: false,
	                cache: false,
	                contentType: false,
	                processData: false,
	                success: function (returnInfo) {
	                    //上传成功后将控件内容清空，并显示上传成功信息
	                    document.getElementById('fileImport').value = null;
	                    document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>" + returnInfo.msg + "</span>";
	                },
	                error: function (xhr,status,returnInfo) {
	                	alert(xhr+'-'+status+'-'+returnInfo);
	                    //上传失败时显示上传失败信息
	                    document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>" + returnInfo.msg + "</span>";
	                }
	            });
	        }
	        else {
	            alert("文件类型错误");
	            //将错误信息显示在前端label文本中
	            document.getElementById('fileName').innerHTML = "<span style='color:Red'>错误提示:上传文件应该是.xlsx后缀而不应该是" + file_typename + ",请重新选择文件</span>"
	        }
	    }
	</script>
    </html>