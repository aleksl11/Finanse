package com.example.finanse

import com.example.finanse.screens.getSortTypeName
import com.example.finanse.screens.getTimePeriodName
import com.example.finanse.sortTypes.CategorySortType
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.sortTypes.SummaryTimePeriod
import org.junit.Assert.assertEquals
import org.junit.Test

class GetNameTest {

    @Test
    fun summaryNameTest(){
        assertEquals("All time", getTimePeriodName(SummaryTimePeriod.ALL_TIME))
        assertEquals("This month", getTimePeriodName(SummaryTimePeriod.THIS_MONTH))
        assertEquals("This year", getTimePeriodName(SummaryTimePeriod.THIS_YEAR))
    }

    @Test
    fun incomeNameTest(){
        assertEquals("Default", getSortTypeName(IncomeSortType.DATE_ADDED))
        assertEquals("Date", getSortTypeName(IncomeSortType.DATE_OF_INCOME))
        assertEquals("Amount", getSortTypeName(IncomeSortType.AMOUNT))
    }

    @Test
    fun expenseNameTest(){
        assertEquals("Default", getSortTypeName(ExpenseSortType.DATE_ADDED))
        assertEquals("Date", getSortTypeName(ExpenseSortType.DATE_OF_INCOME))
        assertEquals("Amount", getSortTypeName(ExpenseSortType.AMOUNT))
        assertEquals("Category", getSortTypeName(ExpenseSortType.CATEGORY))
    }

    @Test
    fun categoryNameTest(){
        assertEquals("Default", getSortTypeName(CategorySortType.DATE_ADDED))
        assertEquals("Name", getSortTypeName(CategorySortType.NAME))
    }
}