document.addEventListener("DOMContentLoaded", function() {
    var employmentContractControl = document.getElementById("employmentContract");
    var reasonForDefiniteControl = document.getElementById("reasonForDefinite");
    var foreignNationalCheckbox = document.getElementById('foreignNational');
    var expiryDateOfWorkPermitInput = document.getElementById('expiryDateOfWorkPermit');

    function toggleReasonForDefinite() {
        if (employmentContractControl.value === "Određeno") {
            reasonForDefiniteControl.disabled = false;
        } else {
            reasonForDefiniteControl.disabled = true;
        }
    }

    function toggleExpiryDateOfWorkPermitInput() {
        if (foreignNationalCheckbox.checked) {
            expiryDateOfWorkPermitInput.disabled = false;
        } else {
            expiryDateOfWorkPermitInput.disabled = true;
            expiryDateOfWorkPermitInput.value = "";
        }
    }

    toggleReasonForDefinite();
    toggleExpiryDateOfWorkPermitInput(); // Dodana linija za poziv funkcije

    employmentContractControl.addEventListener("change", toggleReasonForDefinite);
    foreignNationalCheckbox.addEventListener('change', toggleExpiryDateOfWorkPermitInput);
});
