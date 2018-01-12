<#macro date name id="" value="" labelWidth="80px" labelText="" width="160px" height="22px" options=""
 style="" required="false" after="" before="" afterCurrent="false" beforeCurrnt="false" dateFmt="yyyy-MM-dd">
 	<#assign validate="">
 	<#if after != ''>
 	 	<#assign validate="'afterDate[\\\'${after}\\\']'">
 	</#if>
 	<#if before != ''>
 	 	<#assign validate="'beforeDate[\\\'${before}\\\']',${validate}">
 	</#if>
 	<#if afterCurrent=="true">
 		<#assign validate="'afterCurrentDate',${validate}">
 	</#if>
 	<#if options == "">
		<#assign options2="required:${required},dateFmt:'${dateFmt}',validType:[${validate}]"/>
	</#if>
	<#if required == "true">
		<#assign labelText2="${labelText}<span style=color:red>*</span>"/>
	<#else>
		<#assign labelText2="${labelText}"/>
	</#if>

	<div id="${pid()}" class="custom-field" name="${name}" style="${style}">
		<label style="width:${labelWidth};line-height:${height}" class="font-input">${labelText2}</label>
		<input id="${pid(id)}" type="text" style="height:${height};width:${width}"  name="${name}" value="${value}" 
		class="border easyui-datebox" data-options="${options2}" />
	</div>
 
</#macro>

<#macro datetime  name id="" value="" labelWidth="80px" labelText="" width="160px" height="22px" dateFmt="yyyy-MM-dd HH:mm:ss"
 style="" required="false" after="" before="" afterCurrent="false" beforeCurrnt="false">
	<@p.date name="${name}" id="${id}" labelWidth="${labelWidth}" dateFmt="${dateFmt}"
		labelText="${labelText}" width="${width}" height="${height}"
		style="${style}" required="${required}"  after="${after}" before="${before}" afterCurrent="${afterCurrent}"  beforeCurrnt="${beforeCurrnt}"/>
</#macro>
