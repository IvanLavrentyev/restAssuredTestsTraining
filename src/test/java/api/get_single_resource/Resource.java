package api.get_single_resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Resource {
    private Integer id;
    private String name;
    private Integer year;
    private String color;
    private String pantone_value;
}
