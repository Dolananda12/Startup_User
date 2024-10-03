package com.example.dashboard.data

import com.example.dashboard.model.Affirm
import com.example.startup_user.R

object DataSource {
    var topics =listOf(
        Affirm(R.string.one,R.drawable.user_profile_filled_svgrepo_com),
        Affirm(R.string.two,R.drawable.money_bag_svgrepo_com),
        Affirm(R.string.three,R.drawable.reports_svgrepo_com),
        Affirm(R.string.four,R.drawable.social_instant_message_svgrepo_com),
    )
}