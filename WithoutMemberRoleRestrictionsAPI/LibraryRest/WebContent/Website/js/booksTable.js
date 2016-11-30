
$(document).ready(function() {
	Dropzone.autoDiscover = false;
	//Datatable Creation
   var table= $('#example').DataTable( {
        "ajax": {
            "url": "http://localhost:8080/LibraryRest/api/listBooks",
            "type": "GET"
        }, 
	   "searching": true,
	   "columnDefs": [ {
            "targets":4,
            "data": null,
            "defaultContent": "<button>Click!</button>"
        } ],
        "columns": [
			{ "data": "id" },
            { "data": "Title" },
            { "data": "Author" },
            { "data": "Pages" },
			{ "data": "Cover","orderable": false },
			{ "data": null ,"searchable": false, "orderable": false,
			"defaultContent": " <span class='glyphicon glyphicon-pencil' aria-hidden='true'></span>"},
			{ "data": null ,"searchable": false, "orderable": false,
			"defaultContent": "<span class='glyphicon glyphicon-trash' aria-hidden='true'></span>"}
			
        ]

    } );
	
	
    //Datatable, enabling multi-column search (not compatible with datatables scrolling functionality)
    $('#example tfoot th').each( function () {

        var title = $(this).text();
        $(this).html( '<input type="text" class="form-control" placeholder="Search '+title+'" />' );
    } );
 

 
    // Apply the search
    table.columns().every( function () {
        var that = this;
        $('input', this.footer() ).on( 'keyup change', function () {
           if ( that.search() !== this.value ) {
                that.search( this.value ).draw();
            }
        });
    } );

	//Delete the selected element by clicking its trashcan icon/ deletion node
	
	var tempDelID;
    $(document).on( 'click', '.glyphicon-trash', function () {
	tempDelID=$(this).parents('tr').children(":first").text();
	$('#alertModal').modal('toggle');	
		$("#alert-modal-headline").html("<span class='glyphicon glyphicon-remove'></span> Book: " +tempDelID );
		var prompthtml='<p><font color="red">Do you want to delete this book?</font></p><div class="modal-footer"><button type="submit" class="btn btn-danger btn-default pull-left" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>  <button type="submit" id="confirm-del" class="btn btn-success btn-default pull-right" data-dismiss="modal"><span class="glyphicon glyphicon-edit"></span> Proceed</button> </div>';
        $("#alertContainer").html(prompthtml);
		
        

    } );
	
	 $(document).on( 'click', '#confirm-del', function () {
		$.post("http://localhost:8080/LibraryRest/api/listBooks/removeEntry",
		{
			id: tempDelID
		},
		function(data){
			alert(data.queryResult);
				table.ajax.reload();
		});
	 });
	

	//Displays the cover image in a modal
    $(document).on( 'click', '.resized-img', function () {
        var imgSrc=$(this).parents('tr').find(':nth-child(5)').find('img').attr('src');
		var fullImage="<img class='img-responsive' placeholder='image' src='" + imgSrc+ "'>";
		var bookTitle =$(this).parents('tr').find(':nth-child(2)').text();
		$('#alertModal').modal('toggle');	
		$("#alert-modal-headline").html("<span class='glyphicon glyphicon-picture'></span>" + bookTitle);
        $("#alertContainer").html(fullImage);
    } );
	



  
});

