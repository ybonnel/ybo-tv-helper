package fr.ybo.modele;

import com.google.common.base.Function;
import fr.ybo.xmltv.Programme;

import java.io.Serializable;

public class TransformProgrammes implements Function<Programme, ProgrammeForCouchBase>, Serializable {
    @Override
    public ProgrammeForCouchBase apply(Programme programme) {
        return ProgrammeForCouchBase.fromProgramme(programme);
    }
}
