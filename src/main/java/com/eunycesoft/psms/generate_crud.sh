#!/bin/sh
root="com.eunycesoft.psms"
repository="data/repository"
crud="views/crud"

menu_entries=""
main_source_file="views/MainLayout.java"
menu_entries_delimiter="\/\/ crud menu\n"

if [ ! -d $(dirname $repository) ]; then
  mkdir -p $(dirname $repository)
fi
if [ ! -d $(dirname $crud) ]; then
  mkdir -p $(dirname $crud)
fi
#echo " ******************** Generating repositories, crud views and menu entries ******************** "
#for file in data/entity/*.java data/entity/**/*.java; do
for file in data/entity/*.java ; do
  entityName=$(basename -- "$file" ".java")
#  if [[ $entityName == Abstract* ]]; then
#    continue
#  fi
  entityPath=${file%.java}
  entityImport="$root.${entityPath//\//.}"
  entityPackage=${entityImport%.$entityName}
  repositoryFile="${entityPath/entity/repository}Repository.java"
  crudFile="${entityPath/data\/entity/views\/crud}View.java"
  repositoryPackage=${entityPackage/entity/repository}
  crudPackage=${entityPackage/data.entity/views.crud}

  #  echo "$crudPackage"
  #  continue

  #  Create spring repository
  if [ ! -f "${repositoryFile}" ]; then
    repositoryContent="package ${repositoryPackage};

import ${entityImport};
import com.eunycesoft.psms.data.ExtendedJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entityName}Repository extends ExtendedJpaRepository<${entityName}, Integer> {

}
"
    echo "$repositoryContent" >"$repositoryFile"
  fi

  #  Create crud ui
  if [ ! -f "${crudFile}" ]; then
    crudContent="package ${crudPackage};

import ${entityImport};
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle(\"${entityName}\")
@Route(value = \"${entityName}\", layout = MainLayout.class)
@RolesAllowed(\"ADMIN\")
public class ${entityName}View extends Crud<${entityName}> {
	public ${entityName}View() {
		super(${entityName}.class);
	}
}
"
    echo "$crudContent" >"$crudFile"
  fi

  #  Create crud menu entry
  menu_entries="${menu_entries} new MenuItemInfo(\"$entityName\", VaadinIcon.CARET_RIGHT, ${entityName}View.class),\n"
done

menu_entries=${menu_entries%,\\n}
new_content=$(perl -0pe "s/$menu_entries_delimiter.*$menu_entries_delimiter/$menu_entries_delimiter$menu_entries\n$menu_entries_delimiter/s" $main_source_file)
echo "$new_content" >$main_source_file
