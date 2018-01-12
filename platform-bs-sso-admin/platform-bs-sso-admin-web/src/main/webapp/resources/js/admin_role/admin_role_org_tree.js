var queryUrl = BasePath + "/org_unit";
var ctrl = {}; 
/*$(function() {
	
	//加载用户权限数据（组织）
	initLeftTree(); 
});
 */


function init_tree(id, url, fn) {
	$('#' + id).tree({
		url : url,
		checkbox : true, 
		lines : true,// 显示虚线效果 
		onClick : fn,  
		onLoadSuccess : function(node, data){
		}
	});
}

 
function initLeftTree() {
	var url = BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1';
	init_tree('org-unit-tree', url, function(node) {  
		if(node.state=='closed')
		{
			//当选中该节点的时候，展开该节点下的节点，该方法只会展开下一级节点，并不会展开子节点下的节点。
			$(this).tree('expand', node.target);
		}
//		ctrl.edit(node.id);
//		$('#unit_search').val(node.text);
//		do_tree_search(node.id);
	});
}

function initRoleTree(roleId) {
	var url = BasePath + '/org_unit/tree_data_pre.json?refresh=1&level=1&roleId='+roleId;
	init_tree('org-unit-tree', url, function(node) {  
		if(node.state=='closed')
		{
			$(this).tree('expand', node.target);
		}
//		ctrl.edit(node.id);
//		$('#unit_search').val(node.text);
//		do_tree_search(node.id);
	});
}

