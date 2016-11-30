	
//Creating an UUID to use in a custom generated filename
function generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
};	
	
function getFileExtension(fileName){
	var fileExt=fileName.split(".");
	var i=fileExt.length-1;
	return fileExt[i];
};
	
function generateNewName(fileName) {
	return generateUUID()+"."+getFileExtension(fileName);
};

	  
	//check if input values are filled
  function allFilled() {
		var filled = true;
		$('form input ').each(function() {
			if($(this).val() == '') filled = false;
		});
		return filled;
	}
//Prevent dropzone autodiscover
Dropzone.autoDiscover = false;	
	

 $(document).ready(function(){
/* -----------------------------------------DROPZONE handler*/
// The configuration of our dropzone div 
	 
	var myDropzone = new Dropzone("#uploadme", {
                url: "http://localhost:8080/LibraryRest/api/listBooks/newBook",
                dictDefaultMessage: "Drag the Cover",
                clickable: true,
                enqueueForUpload: false,
                maxFiles: 1,
                uploadMultiple: false,
                addRemoveLinks: true,
				autoProcessQueue: false,
				acceptedFiles: "image/jpeg,image/png,image/gif",
				accept: function(file, done) {
					done();
				  },
				init: function() {
					var newFilename;
					this.on("processing", function(file) {
						
					});
					
					this.on("addedfile", function(file) {
						newFilename=generateNewName(file.name);
						$(" #Cover").val(newFilename);
						if (this.files[1]!=null){
							alert("You can not upload more than one image, Cover will be overwritten");
							this.removeFile(this.files[0]);
						}
						
						var fileExtension=getFileExtension(file.name);
						if(fileExtension!=="jpg" && fileExtension!=="png" && fileExtension!=="gif")
						{
							alert("Please upload a valid png, gif or jpg image for yor cover");
							this.removeFile(this.files[0]);
						}
						if(allFilled() && coverSelected())
							$('#newbookform').removeAttr('disabled');
						else
							$('#newbookform').attr('disabled', 'disabled');
						
					});
					
					
					this.on("removedfile", function(file) {
							$(" #Cover").val('');
							$('#newbookform').attr('disabled', 'disabled');
					});
					
					
					this.on('sending', function(file, xhr, formData){
						console.log($(".dropzone-submit").attr('id'));

							formData.append('ID',$(" #modal-form-id").val());
							formData.append('Title',$(" #Title").val());
							formData.append('Author',$(" #Author").val());
							formData.append('Description',$(" #Description").val());
							formData.append('Pages',0+$(" #Pages").val());
							formData.append('Cover',newFilename);
							
						});
					this.on("success", function(file, responseText) {
					popAlertModal("Success",responseText.queryResult,'green');
    				table.ajax.reload();
					});
					this.on("error", function(file, response) {
    				table.ajax.reload();
					});

						$("#newbookform").click(function(e){
							if($(".dropzone-submit").attr('id')==='updatebookform'){
							myDropzone.options.url = "http://localhost:8080/LibraryRest/api/listBooks/updateBook";
							if(!coverSelected()){
								updateWithoutFile(e);
							}	
							 
						}
							else
							myDropzone.options.url = "http://localhost:8080/LibraryRest/api/listBooks/newBook";
							$('#myModal').modal('toggle');
							table.ajax.reload();
							e.preventDefault();
							 e.stopPropagation();
							 myDropzone.processQueue(); // Tell Dropzone to process all queued files.
						});

			  }	
	});

	  
	      // DataTable variable
    		var table = $('#example').DataTable();
	  	
			  function coverSelected(){
				return(myDropzone.files.length>0);
				}
				//Trigger the ajax request without uploading an image
			  function updateWithoutFile(e){
				var formData = new FormData();


			var formURL =  "http://localhost:8080/LibraryRest/api/listBooks/updateBook";

			formData.append('ID',$(" #modal-form-id").val());
			formData.append('Title',$(" #Title").val());
			formData.append('Author',$(" #Author").val());
			formData.append('Description',$(" #Description").val());
			formData.append('Pages',0+$(" #Pages").val());
			formData.append('Cover',"");
			  $.ajax(
					 {
				url : formURL,
				type: "POST",
				data: formData,
				contentType: false,
    			processData: false,
				success:function(data, textStatus, jqXHR) 
				{
					popAlertModal("Success",data.queryResult,'green');
					table.ajax.reload();
				},
				error: function(textStatus, errorThrown) 
				{
					
					table.ajax.reload();
					popAlertModal("Error",'Something happened in the server','red');
				}
		  		});
			  e.preventDefault();
			  e.stopPropagation();
		}


		//Validating New book fields - New book modal
		$('form input').bind('keyup', function() {
			if(allFilled() && coverSelected()){
				$('#newbookform').removeAttr('disabled');
			} 
			else
				$('#newbookform').attr('disabled', 'disabled');
		});

		 //Set the modal to an insertion form or an update form
		$(document).on( 'click', '.glyphicon-pencil', function () {
			if(coverSelected){
					Dropzone.forElement("#uploadme").removeAllFiles(true);
				}
			var modifyId=$(this).parents('tr').children(":first").text();
			var modifyAuthor=$(this).parents('tr').children(":nth-child(3)").text();
			var modifyTitle=$(this).parents('tr').children(":nth-child(2)").text();
			var modifyPages=$(this).parents('tr').children(":nth-child(4)").text();
			$(" #Author").val(modifyAuthor);
			$(" #Title").val(modifyTitle);
			$(" #Pages").val(modifyPages);
				
			$('#book-modal-headline').html('<span class="glyphicon glyphicon-book"></span> Update book with id: ' + modifyId);
			$('#modal-form-id').prop("value",modifyId);
			$('#newbookform').html('<span class="glyphicon glyphicon-edit"></span> Update book');
			$('#newbookform').prop('disabled', false);
			$('#newbookform').prop('id', 'updatebookform');
			$('#myModal').modal('toggle');

		} );

		$(document).on( 'click', '#book-modal-button', function () {
			if(coverSelected){
					Dropzone.forElement("#uploadme").removeAllFiles(true);
				}
			$(" #Author").val("");
			$(" #Title").val("");
			$(" #Pages").val(0);
			$(" #Description").val("");
			$('#book-modal-headline').html('<span class="glyphicon glyphicon-book"></span> Insert Book');
			$('#updatebookform').html('<span class="glyphicon glyphicon-edit"></span> Insert book');
			$('#updatebookform').prop('id', 'newbookform');
			$('#newbookform').prop('disabled', true);

		} );
	 
	 	function popAlertModal(Title,textDisplay,textColor){
			$('#alertModal').modal('toggle');	
		$("#alert-modal-headline").html("<span class='glyphicon glyphicons-play'></span>" + Title );
		var prompthtml='<p><font color="'+textColor+' ">'+textDisplay+'</font></p><div class="modal-footer"><button type="submit" class="btn btn-danger btn-default pull-left" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Close</button>  </div>';
        $("#alertContainer").html(prompthtml);
		}
 
});