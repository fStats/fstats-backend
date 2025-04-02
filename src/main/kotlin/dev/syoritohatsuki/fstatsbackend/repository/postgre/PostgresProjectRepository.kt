package dev.syoritohatsuki.fstatsbackend.repository.postgre

import dev.syoritohatsuki.fstatsbackend.db.ProjectsTable
import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.FAILED
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.dbQuery
import dev.syoritohatsuki.fstatsbackend.repository.ProjectRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

object PostgresProjectRepository : ProjectRepository {
    override suspend fun create(name: String, ownerId: Int): Int = dbQuery {
        val insertResult = ProjectsTable.insert {
            it[ProjectsTable.name] = name
            it[ProjectsTable.ownerId] = ownerId
        }
        if (insertResult.insertedCount > 0) SUCCESS else FAILED
    }

    override suspend fun deleteById(id: Int): Int = dbQuery {
        val deleteResult = ProjectsTable.deleteWhere { ProjectsTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILED
    }

    override suspend fun getAll(): List<Project> = dbQuery {
        (ProjectsTable innerJoin UsersTable).selectAll().map(Project::fromResultRow)
    }

    override suspend fun getByOwner(ownerId: Int): List<Project> = dbQuery {
        (ProjectsTable innerJoin UsersTable).selectAll().where { ProjectsTable.ownerId eq ownerId }
            .map(Project::fromResultRow)
    }

    override suspend fun getById(id: Int): Project? = dbQuery {
        (ProjectsTable innerJoin UsersTable).selectAll().where { ProjectsTable.id eq id }
            .mapNotNull(Project::fromResultRow).singleOrNull()
    }

    override suspend fun updateProjectData(projectId: Int, project: Project): Int {
        if (project.name.isBlank() && project.isHidden == null) return FAILED

        return dbQuery {
            ProjectsTable.update({ ProjectsTable.id eq projectId }) {
                if (project.name.isNotBlank()) it[name] = project.name
                if (project.isHidden != null) it[isHidden] = project.isHidden
            }
        }
    }
}
