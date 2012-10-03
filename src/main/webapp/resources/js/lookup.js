 var displayableProperties = ["name"];
 $(document).ready(function() {
    $('#lookup').click(function(){
        $('#result').hide();
        $('#spinner').show();
        $('#error').hide();
        var inputPhoneNumber = $('#inputPhoneNumber').val();
        $.ajax({
            url: "lookup",
            data: { country: $('#country').val(), num: inputPhoneNumber },
            dataType: 'json',
            success: function(data) {
                $('#name').text(data.name);
                $('#company').text(data.name == data.company || data.company == null ? '' : data.company);
                $('#address').text(data.name == data.address || data.address == null ? '' : data.address);
                $('#phoneNumber').text(data.phoneNumber);
                $('#result').fadeIn('fast');
            },
            error: function(jqXHR, textStatus, errorThrown){
                $('#error').text(textStatus).fadeIn('fast');
            },
            complete: function(){
                $('#spinner').hide();
            }
        });
    });
 });
 
