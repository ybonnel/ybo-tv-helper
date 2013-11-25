package fr.ybo.modele;

import com.google.common.base.Function;
import fr.ybo.xmltv.Programme;

import java.io.Serializable;

public class TransformProgrammes implements Function<Programme, ProgrammeForNoSql>, Serializable {
    @Override
    public ProgrammeForNoSql apply(Programme programme) {
        return ProgrammeForNoSql.fromProgramme(programme);
    }
}
