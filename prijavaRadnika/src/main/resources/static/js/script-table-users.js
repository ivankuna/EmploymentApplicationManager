$(document).ready(function() {
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
        "columnDefs": [ {
            "targets": "_all",
            "render": function ( data, type, row ) {
                if (data != null) {
                if (data === "true") {
                    return "DA";
                } else if (data === "false") {
                    return "NE";
                } else {
                    return data;
                }
            }
            }
        } ]
    } );
} );