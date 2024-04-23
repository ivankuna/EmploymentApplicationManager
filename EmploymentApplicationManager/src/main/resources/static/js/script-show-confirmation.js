function showConfirmation() {
        if (confirm("Želite li prekinuti unos?")) {
            window.location = "[[${path}]]";
        } else {
            return false;
        }
    }