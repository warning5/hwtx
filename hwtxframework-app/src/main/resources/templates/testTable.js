<script type="text/javascript">
function refreshTable(){
	var oTable = $('#bizTestTable').dataTable();
	oCache.iCacheLower = -1;
	oTable.fnDraw(false);
}

$(document).ready(function() {
    initTablePipeline();
    $('#bizTestTable').dataTable({
        "bProcessing": true,
        "bServerSide": true,
        "bFilter": false,
        "bLengthChange": false,
        "sPaginationType" : "bootstrap_full",
        "sAjaxSource": ^ajaxSource^,
        "sServerMethod": "POST",
        "iDisplayLength": 5,
        "fnServerData": fnDataTablesPipeline,
        "fnServerParams": function ( aoData ) {
        	^params^
        },
        "aoColumns": [^columns^] 
    });
});
</script>