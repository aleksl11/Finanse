package com.example.finanse

import android.content.Context
import com.example.finanse.screens.getSortTypeName
import com.example.finanse.screens.getTimePeriodName
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.sortTypes.SummaryTimePeriod
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class GetNameTest {

    @Test
    fun summaryNameTest(){
        val context = mockk<Context>()

        every { context.getString(R.string.all_time) } returns "All time"
        every { context.getString(R.string.this_month) } returns "This month"
        every { context.getString(R.string.this_year) } returns "This year"

        assertEquals("All time", getTimePeriodName(context, SummaryTimePeriod.ALL_TIME))
        assertEquals("This month", getTimePeriodName(context, SummaryTimePeriod.THIS_MONTH))
        assertEquals("This year", getTimePeriodName(context, SummaryTimePeriod.THIS_YEAR))
    }

    @Test
    fun incomeNameTest() {
        val context = mockk<Context>()

        every { context.getString(R.string.sort_by_default) } returns "Default"
        every { context.getString(R.string.sort_by_date) } returns "Date"
        every { context.getString(R.string.sort_by_amount) } returns "Amount"

        assertEquals("Default", getSortTypeName(context, IncomeSortType.DATE_ADDED))
        assertEquals("Date", getSortTypeName(context, IncomeSortType.DATE_OF_INCOME))
        assertEquals("Amount", getSortTypeName(context, IncomeSortType.AMOUNT))
    }

    @Test
    fun expenseNameTest() {
        val context = mockk<Context>()

        every { context.getString(R.string.sort_by_default) } returns "Default"
        every { context.getString(R.string.sort_by_date) } returns "Date"
        every { context.getString(R.string.sort_by_amount) } returns "Amount"
        every { context.getString(R.string.sort_by_category) } returns "Category"

        assertEquals("Default", getSortTypeName(context, ExpenseSortType.DATE_ADDED))
        assertEquals("Date", getSortTypeName(context, ExpenseSortType.DATE_OF_INCOME))
        assertEquals("Amount", getSortTypeName(context, ExpenseSortType.AMOUNT))
        assertEquals("Category", getSortTypeName(context, ExpenseSortType.CATEGORY))
    }

    @Test
    fun categoryNameTest() {
        val context = mockk<Context>()

        every { context.getString(R.string.sort_by_default) } returns "Default"
        every { context.getString(R.string.sort_by_name) } returns "Name"

        assertEquals("Default", getSortTypeName(context, CategorySortType.DATE_ADDED))
        assertEquals("Name", getSortTypeName(context, CategorySortType.NAME))
    }
}