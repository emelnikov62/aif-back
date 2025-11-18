var today = new Date();
var selected = [];

$(document).ready(function () {
    const data = JSON.stringify({allow_vertical_swipe: false});
    window.TelegramWebviewProxy.postEvent('web_app_setup_swipe_behavior', data);

    $('#close_button').click(function () {
        window.Telegram.WebApp.close();
    });

    fillCalendar(today);

    $('.prev-btn').click(() => {
        today.setMonth(today.getMonth() - 1);
        fillCalendar(today);
    });

    $('.next-btn').click(() => {
        today.setMonth(today.getMonth() + 1);
        fillCalendar(today);
    });

    $('#all_select_button').click(() => {
        $('#save_button').removeClass('hide-block');
        $('#cancel_select_button').removeClass('hide-block');
        selected = [];
        $('.day-container').each((elem) => {
            if ($($('.day-container')[elem]).attr('data-day')) {
                $($('.day-container')[elem]).addClass('selected-day');
                selected.push($($('.day-container')[elem]).attr('data-day'));
            }
        });
    });

    $('#cancel_select_button').click(() => {
        $('.day-container').removeClass('selected-day');
        selected = [];
        $('#cancel_select_button').addClass('hide-block');
        $('#save_button').addClass('hide-block');
    });
});

function fillCalendar(today) {
    changeCurrentDate(today);
    fillMonthCalendar(today.getMonth(), today.getFullYear());
}

function changeCurrentDate(today) {
    $('.current-date').text(`${monthNames[today.getMonth()]} ${today.getFullYear()}`);
}

function fillMonthCalendar(month, year) {
    $('.days').text('');
    $('.day-container').addClass('disabled-back');
    var date = new Date(year, month);
    date.setDate(1);

    var i = 0;
    var week = 1;
    var day = date.getDay();
    while (date.getMonth() == month) {
        day = date.getDay() == 0 ? 7 : date.getDay();
        $(`.week-${week} .day-${day}`).text(date.getDate());
        $(`.week-${week} .day-container-${day}`).removeClass('disabled-back');
        $(`.week-${week} .day-container-${day}`).attr('data-day', date.getDate());

        $(`.week-${week} .day-container-${day}`).click((elem) => {
            if ($(elem.target).attr('data-day')) {
                var selectedDay = $(elem.target).attr('data-day');

                if ($(elem.target).hasClass('selected-day')) {
                    $(elem.target).removeClass('selected-day');
                    selected.splice(selected.findIndex((sel) => sel == selectedDay), 1);
                } else {
                    $(elem.target).addClass('selected-day');
                    selected.push(selectedDay);
                }

                if (selected.length == 0) {
                    $('#cancel_select_button').addClass('hide-block');
                    $('#save_button').addClass('hide-block');
                } else {
                    $('#cancel_select_button').removeClass('hide-block');
                    $('#save_button').removeClass('hide-block');
                }
            }
        });

        if (day == 7) {
            week = week + 1;
        }

        date.setDate(date.getDate() + 1);
    }
}