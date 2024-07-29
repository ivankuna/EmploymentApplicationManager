
    $(document).ready(function() {
        const fixedMessage = 'Tehnička podrška: +385 (91) 227-5504';

        $('#logoutLink').click(function(e) {
            e.preventDefault();
            alert(fixedMessage);
        });
    });
