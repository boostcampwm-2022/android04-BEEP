package com.lighthouse.datasource.brand

import com.lighthouse.database.dao.BrandDao
import javax.inject.Inject

class BrandLocalDataSourceImpl @Inject constructor(
    private val brandDao: BrandDao
) : BrandLocalDataSource
