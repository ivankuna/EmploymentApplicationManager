$(document).ready(function() {

    var dateColumnIndex = 6;

    $('#myTable').DataTable( {
        "language": {
            "lengthMenu": "Prikaži _MENU_ zapisa po stranici",
            "zeroRecords": "Nema pronađenih zapisa",
            "info": "Stranica _PAGE_ od _PAGES_",
            "infoEmpty": "Nema dostupnih zapisa",
            "infoFiltered": "",
            "search": "Traži:",
            "paginate": {
                "first": "Prva",
                "previous": "Prethodna",
                "next": "Slijedeća",
                "last": "Zadnja"
            },
            "emptyTable": "Nema dostupnih podataka u tablici",
            "loadingRecords": "Učitavanje...",
            "processing": "Obrada..."
        },
        "columnDefs": [
            {
                "targets": "_all",
                "render": function (data, type, row) {
                if (data === "true") {
                                        return '<i class="fas fa-envelope"></i>';
                                    } else if (data === "false") {
                                        return '<i class="fas fa-circle"></i>';
                                    } else {
                   if (data) {
                       if (moment(data, 'YYYY-MM-DD').isValid() && !/[a-zA-Z]/.test(data) && data.length === 10 && data.indexOf('-') !== 2) {
                           var date = moment(data, 'YYYY-MM-DD');
                           var minDate = moment('1999-12-31', 'YYYY-MM-DD');
                           if (date.isAfter(minDate)) {
                               return date.format('DD-MM-YY');
                           } else {
                               return data;
                           }
                       } else {
                           return data;
                       }
                   } else {
                       return '';
                   }

                }
                }
            }
        ]
    } );
} );