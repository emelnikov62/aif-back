$(document).ready(function () {
    const data = JSON.stringify({allow_vertical_swipe: false});
    // window.TelegramWebviewProxy.postEvent('web_app_setup_swipe_behavior', data);

    $('.add-item-btn').click(function () {
        $('.item-form').removeClass('hide-block');
        $('.add-item-btn').addClass('hide-block');
    });

    $('.cancel-btn').click(function () {
        $('.item-form').addClass('hide-block');
        $('.add-item-btn').removeClass('hide-block');

        $('.name-item').val('');
        $('.hours-item').val('');
        $('.mins-item').val('');
        $('.amount-item').val('');

        var id = $('#bot_id').val();

        // $.get(`https://aif-back-emelnikov62.amvera.io/aif/admin/link-bot?id=${id}&token=${token}`).done(function (data) {
        //     if (data) {
        //         showAlert('success', 'TOKEN бота привязан');
        //         setTimeout(() => {
        //             window.Telegram.WebApp.close();
        //         }, 2000);
        //     } else {
        //         showAlert('error', 'Произошла ошибка. Попробуйте позже');
        //     }
        // });
    });
});