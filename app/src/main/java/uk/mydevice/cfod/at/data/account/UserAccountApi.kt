package uk.mydevice.cfod.at.data.account

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
        var account: Account? = null
        for (item in accounts) {
            if (item.name.endsWith("field.census.gov.uk") || item.name.endsWith("FIELD.CENSUS.GOV.UK")) {
                account = item
            }
        }
        return account
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
