/**
*
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL_V2-fr.txt
*        see Licence_CeCILL_V2-en.txt
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
* 
* 
* @copyright IGN
*
*/
package fr.ign.cogit.process.geoxygene.cartetopo;

import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.ppio.CarteTopoResult;

/**
 * 
 * 
 *
 */
@DescribeProcess(title = "CreateCarteTopoProcess", description = "Create ")
public class CreateCarteTopoProcess implements GeoxygeneProcess {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(CreateCarteTopoProcess.class.getName());

    /**
     * 
     * @param popRef
     * @return
     */
    @DescribeResult(name = "CarteTopo", description = "Carte topologique")
    public CarteTopoResult execute(
        @DescribeParameter(name = "rawDataset", description = "Raw data") SimpleFeatureCollection rawDataset,
        @DescribeParameter(name = "tolerance", description = "Tolerance") double tolerance) {
        
        LOGGER.debug("Create cartetopo begin process.");
        
        CarteTopo networkMap = new CarteTopo("Network Map");
        
        // Convert raw data to GeOxygeneCollection
        IFeatureCollection<?> edges = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(rawDataset);
        
        // A passer en paramètres
        String orientationAttribute = "";
        Map<Object, Integer> orientationMap = null;
        String filterAttribute = "";
        Map<Object, Boolean> filterMap = null;
        String groundPositionAttribute = "";
        
        Chargeur.importAsEdges(edges, networkMap, orientationAttribute, 
                orientationMap, filterAttribute, filterMap, groundPositionAttribute, tolerance);
        
        // Result
        CarteTopoResult result = new CarteTopoResult();
        result.setPopArc(networkMap.getPopArcs());
        result.setPopNoeud(networkMap.getPopNoeuds());
        
        // return carte topo
        return result;
    }

}
