package com.lighthouse.data.database.ext

import com.lighthouse.beep.model.result.DbResult
import com.lighthouse.data.database.exception.DeleteException
import com.lighthouse.data.database.exception.InsertException
import com.lighthouse.data.database.exception.NotFoundException
import com.lighthouse.data.database.exception.SelectException
import com.lighthouse.data.database.exception.UpdateException

internal inline fun <T, R> T.runCatchingDB(block: T.() -> R): DbResult<R> {
    return try {
        DbResult.Success(block())
    } catch (e: NotFoundException) {
        DbResult.NotFoundError(e)
    } catch (e: SelectException) {
        DbResult.SelectError(e)
    } catch (e: InsertException) {
        DbResult.InsertError(e)
    } catch (e: DeleteException) {
        DbResult.DeleteError(e)
    } catch (e: UpdateException) {
        DbResult.UpdateError(e)
    } catch (e: Throwable) {
        DbResult.Failure(e)
    }
}
