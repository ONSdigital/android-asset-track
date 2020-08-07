package uk.gov.ons.census.cfod.at.data.account

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserAccountApi @Inject constructor(@ApplicationContext context: Context) {

    private val accountManager = AccountManager.get(context)

    /**
     * returns first account from accounts
     */
    private fun getAccount(): Account? {
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE_GOOGLE)
        return if (accounts.isNotEmpty()) accounts[0] else null
    }

    /**
     * returns e-mail address from account
     */
    fun getEmail(): String {
        val account = getAccount()
        return account?.name ?: ""
    }

    companion object {
        const val ACCOUNT_TYPE_GOOGLE = "com.google"
    }
}