package com.lighthouse.data.database.ext

import com.lighthouse.beep.model.exception.db.DeleteException
import com.lighthouse.beep.model.exception.db.InsertException
import com.lighthouse.beep.model.exception.db.NotFoundException
import com.lighthouse.beep.model.exception.db.SelectException
import com.lighthouse.beep.model.exception.db.UpdateException
import com.lighthouse.data.database.exception.DBDeleteException
import com.lighthouse.data.database.exception.DBInsertException
import com.lighthouse.data.database.exception.DBNotFoundException
import com.lighthouse.data.database.exception.DBSelectException
import com.lighthouse.data.database.exception.DBUpdateException

internal inline fun <T, R> T.runCatchingDB(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: DBNotFoundException) {
        Result.failure(NotFoundException(e.message))
    } catch (e: DBSelectException) {
        Result.failure(SelectException(e.message))
    } catch (e: DBInsertException) {
        Result.failure(InsertException(e.message))
    } catch (e: DBDeleteException) {
        Result.failure(DeleteException(e.message))
    } catch (e: DBUpdateException) {
        Result.failure(UpdateException(e.message))
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
