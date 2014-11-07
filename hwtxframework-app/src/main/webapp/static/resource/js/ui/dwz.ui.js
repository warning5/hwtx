function initUI(_box){
	var $p = $(_box || document);

//	$("div.panel", $p).jPanel();
//
//	//tables
//	$("table.table", $p).jTable();
//	
//	// css tables
//	$('table.list', $p).cssTable();
//
//	//auto bind tabs
//	$("div.tabs", $p).each(function(){
//		var $this = $(this);
//		var options = {};
//		options.currentIndex = $this.attr("currentIndex") || 0;
//		options.eventType = $this.attr("eventType") || "click";
//		$this.tabs(options);
//	});
//
//	$("ul.tree", $p).jTree();
//	$('div.accordion', $p).each(function(){
//		var $this = $(this);
//		$this.accordion({fillSpace:$this.attr("fillSpace"),alwaysOpen:true,active:0});
//	});

//	$(":button.checkboxCtrl, :checkbox.checkboxCtrl", $p).checkboxCtrl($p);
	
	if ($.fn.combox) $("select.combox",$p).combox();
	
//	if ($.fn.xheditor) {
//		$("textarea.editor", $p).each(function(){
//			var $this = $(this);
//			var op = {html5Upload:false, skin: 'vista',tools: $this.attr("tools") || 'full'};
//			var upAttrs = [
//				["upLinkUrl","upLinkExt","zip,rar,txt"],
//				["upImgUrl","upImgExt","jpg,jpeg,gif,png"],
//				["upFlashUrl","upFlashExt","swf"],
//				["upMediaUrl","upMediaExt","avi"]
//			];
//			
//			$(upAttrs).each(function(i){
//				var urlAttr = upAttrs[i][0];
//				var extAttr = upAttrs[i][1];
//				
//				if ($this.attr(urlAttr)) {
//					op[urlAttr] = $this.attr(urlAttr);
//					op[extAttr] = $this.attr(extAttr) || upAttrs[i][2];
//				}
//			});
//			
//			$this.xheditor(op);
//		});
//	}
	
//	if ($.fn.uploadify) {
//		$(":file[uploader]", $p).each(function(){
//			var $this = $(this);
//			var options = {
//				uploader: $this.attr("uploader"),
//				script: $this.attr("script"),
//				buttonImg: $this.attr("buttonImg"),
//				cancelImg: $this.attr("cancelImg"),
//				queueID: $this.attr("fileQueue") || "fileQueue",
//				fileDesc: $this.attr("fileDesc"),
//				fileExt : $this.attr("fileExt"),
//				folder	: $this.attr("folder"),
//				fileDataName: $this.attr("name") || "file",
//				auto: $this.attr("auto") || false,
//				multi: true,
//				onError:uploadifyError,
//				onComplete: uploadifyComplete,
//				onAllComplete: uploadifyAllComplete
//			};
//			if ($this.attr("onComplete")) {
//				options.onComplete = DWZ.jsonEval($this.attr("onComplete"));
//			}
//			if ($this.attr("onAllComplete")) {
//				options.onAllComplete = DWZ.jsonEval($this.attr("onAllComplete"));
//			}
//			if ($this.attr("scriptData")) {
//				options.scriptData = DWZ.jsonEval($this.attr("scriptData"));
//			}
//			$this.uploadify(options);
//		});
//	}
	
	// init styles
//	$("input[type=text], input[type=password], textarea", $p).addClass("textInput").focusClass("focus");
//
//	$("input[readonly], textarea[readonly]", $p).addClass("readonly");
//	$("input[disabled=true], textarea[disabled=true]", $p).addClass("disabled");
//
//	$("input[type=text]", $p).not("div.tabs input[type=text]", $p).filter("[alt]").inputAlert();
//
//	//Grid ToolBar
//	$("div.panelBar li, div.panelBar", $p).hoverClass("hover");
//
//	//Button
//	$("div.button", $p).hoverClass("buttonHover");
//	$("div.buttonActive", $p).hoverClass("buttonActiveHover");
	
//	$("form.required-validate", $p).each(function(){
//		var $form = $(this);
//		$form.validate({
//			onsubmit: false,
//			focusInvalid: false,
//			focusCleanup: true,
//			errorElement: "span",
//			ignore:".ignore",
//			invalidHandler: function(form, validator) {
//				var errors = validator.numberOfInvalids();
//				if (errors) {
//					var message = DWZ.msg("validateFormError",[errors]);
//					alertMsg.error(message);
//				} 
//			}
//		});
//		
//		$form.find('input[customvalid]').each(function(){
//			var $input = $(this);
//			$input.rules("add", {
//				customvalid: $input.attr("customvalid")
//			})
//		});
//	});
}