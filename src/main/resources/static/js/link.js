$(document).ready(function () {
    const data = JSON.stringify({allow_vertical_swipe: false});
    window.TelegramWebviewProxy.postEvent('web_app_setup_swipe_behavior', data);

    $('#link_button').click(function () {
        var id = $('#bot_id').val();
        var token = $('#token').val();

        if (!id || !token) {
            showAlert('error', 'Введите TOKEN бота');
            return;
        }

        $.get(`/aif/admin/link-bot?id=${id}&token=${token}`).done(function (data) {
            if (data) {
                showAlert('success', 'TOKEN бота привязан');
                setTimeout(() => {
                    window.Telegram.WebApp.close();
                }, 2000);
            } else {
                showAlert('error', 'Произошла ошибка. Попробуйте позже');
            }
        });
    });
});