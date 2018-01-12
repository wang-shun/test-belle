var queryUrl = BasePath + "/org_unit";
var ctrl = {}; 
$(function() {
	
	//加载用户权限数据（组织）
	initLeftTree(); 
});
 


function init_tree(id, url, fn) {
	$('#' + id).tree({
		url : url,
		checkbox : false, 
		lines : true,// 显示虚线效果 
		onClick : fn,  
		onLoadSuccess : function(node, data){
			 
		}
	});
}

 
function initLeftTree() {
	var url = BasePath + '/org_unit/tree_data_pre.json?level=1';
	init_tree('tt2', url, function(node) {  
		if(node.state=='closed')
		{
			$(this).tree('expand', node.target);
		}
		ctrl.edit(node.id);
		$('#unit_search').val(node.text);
		do_tree_search(node.id);
	});
}

