/**
 * Created with JackHou
 * Date: 2021/3/20
 * Time: 11:15
 * Description:
 */

package com.qtimes.jectpackdemokotlin.model

import org.jetbrains.annotations.NotNull
import javax.annotation.Nullable


class User(
    @NotNull val account: String,
    @NotNull val password: String,
    @Nullable val phone: String = ""
)