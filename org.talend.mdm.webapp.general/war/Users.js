talend.usersandroles = {};
talend.usersandroles.Users = function(){
	
	function _getUrl(language, callBack){
		var frameUrl = "/usersandroles/Users.html";
		callBack(frameUrl);
	}
	
	return {
		getUrl : function(language, callBack){_getUrl(language, callBack);}
	}
}();