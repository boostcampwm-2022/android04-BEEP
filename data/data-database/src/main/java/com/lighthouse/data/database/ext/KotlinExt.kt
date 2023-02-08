package com.lighthouse.data.database.ext

import com.lighthouse.beep.model.exception.db.DeleteException
import com.lighthouse.beep.model.exception.db.InsertException
import com.lighthouse.beep.model.exception.db.NotFoundException
import com.lighthouse.beep.model.exception.db.SelectException
import com.lighthouse.beep.model.exception.db.UpdateException
import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.data.database.exception.DBDeleteException
import com.lighthouse.data.database.exception.DBInsertException
import com.lighthouse.data.database.exception.DBNotFoundException
import com.lighthouse.data.database.exception.DBSelectException
import com.lighthouse.data.database.exception.DBUpdateException

internal inline fun <T, R> T.runCatchingDB(block: T.() -> R): DbResult<R> {
    return try {
        DbResult.Success(block())
    } catch (e: DBNotFoundException) {
        DbResult.Failure(NotFoundException(e.message))
    } catch (e: DBSelectException) {
        DbResult.Failure(SelectException(e.message))
    } catch (e: DBInsertException) {
        DbResult.Failure(InsertException(e.message))
    } catch (e: DBDeleteException) {
        DbResult.Failure(DeleteException(e.message))
    } catch (e: DBUpdateException) {
        DbResult.Failure(UpdateException(e.message))
    } catch (e: Throwable) {
        DbResult.Failure(e)
    }
}
